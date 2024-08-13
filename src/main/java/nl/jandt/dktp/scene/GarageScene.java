package nl.jandt.dktp.scene;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.network.packet.server.play.DamageEventPacket;
import net.minestom.server.network.packet.server.play.HitAnimationPacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import nl.jandt.dktp.Game;
import nl.jandt.dktp.entity.InteractEntity;
import nl.jandt.dktp.entity.ItemDisplayEntity;
import nl.jandt.dktp.poison.Poison;
import nl.jandt.dktp.poison.PoisonIngredient;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class GarageScene extends BaseScene {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private static final Pos spawnPos = new Pos(26.5, 1.1, 0.5, 90, 0);
    private static final Pos poisonPos = new Pos(24.82, 2.60, 0.5, 270, 0);
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();
    private final int seed;
    private final Random random;

    private final Map<PoisonIngredient, Pos> ingredients = new HashMap<>();
    private Poison poison;
    private ItemDisplayEntity poisonDisplay;

    public GarageScene(CustomPlayer player, int seed) {
        super("garage-scene", player);

        this.seed = seed;
        this.random = new Random(seed);

        resetPoison();

        eventNode().addListener(PlayerBlockInteractEvent.class, this::blockInteractEvent);
        eventNode().addListener(PlayerEntityInteractEvent.class, this::entityInteractEvent);
        eventNode().addListener(PlayerTickEvent.class, this::playerTickEvent);

        setupIngredients();
        spawnEntities();
    }

    private void playerTickEvent(@NotNull PlayerTickEvent event) {
        if (event.getPlayer() != getPlayer()) return;

        final var player = (CustomPlayer) event.getPlayer();
        final var losEntity = player.getLineOfSightEntity(3, e -> true);

        Component tooltip = null;

        final var losPoint = player.getTargetBlockPosition(3);
        if (losPoint != null && getInstance().getBlock(losPoint) != Block.AIR) {
            final var losBlock = getInstance().getBlock(losPoint);

            if (losBlock.compare(Block.IRON_TRAPDOOR)) tooltip = mm.deserialize("<#ffbbbb><b>Try your poison</b> \uD83E\uDC9A");
            if (losBlock.compare(Block.CHEST)) tooltip = mm.deserialize("<#aaaaaa>Open chest");
            if (losBlock.compare(Block.BREWING_STAND)) tooltip = mm.deserialize("<#99ffdd>Mix your poison");
            if (losBlock.compare(Block.BARREL)) tooltip = mm.deserialize("<#aadd77>Check barrel");
            if (losBlock.compare(Block.BEEHIVE)) tooltip = mm.deserialize("<#ddaa77>Check box");
        }

        if (losEntity != null && losEntity.hasTag(Tag.Component("Tooltip"))) {
            tooltip = losEntity.getTag(Tag.Component("Tooltip"));
        }

        if (tooltip != null) {
            if (interactionsLocked()) {
                if (losEntity == null || (losEntity instanceof InteractEntity interaction
                        && !interaction.doesAllowWhileLocked())) {
                    tooltip = tooltip
                            .decorate(TextDecoration.STRIKETHROUGH)
                            .color(TextColor.color(170, 170, 170));
                }
            }
            player.sendActionBar(tooltip);
        }
        else player.sendActionBar(Component.empty());
    }

    private void blockInteractEvent(@NotNull PlayerBlockInteractEvent event) {
        if (event.getPlayer() != getPlayer() || !isActive()) return;

        if (interactionsLocked()) return;

        final var player = (CustomPlayer) event.getPlayer();
        final var block = event.getBlock();

        if (block.compare(Block.IRON_TRAPDOOR)) {
            Game.getSceneManager().switchScene(getPlayer(), PresidentScene.class, new SceneContext(poison, null));
        } else if (block.compare(Block.CHEST)) {
            player.sendPacket(new BlockActionPacket(event.getBlockPosition(), (byte) 1, (byte) 1, Block.CHEST));
            scheduler.scheduleTask(() -> player.sendPacket(new BlockActionPacket(event.getBlockPosition(), (byte) 1, (byte) 0, Block.CHEST)),
                    TaskSchedule.seconds(5), TaskSchedule.stop());
        } else if (block.compare(Block.BREWING_STAND)) {
            mixPoison(player);
        } else if (block.compare(Block.BARREL)) {
            searchBlock(event, Key.key("block.barrel.open"), Map.of(
                    new PoisonIngredient(mm("<#669979>Your great-grandma's stock"),
                            Material.SUSPICIOUS_STEW, 3), 0.2f,
                    new PoisonIngredient(mm("<#592029>The family meatloaf"),
                            Material.BREAD, 2), 0.2f
            ));
        } else if (block.compare(Block.BEEHIVE)) {
            lockInteractions(true);
            searchBlock(event, Key.key("block.barrel.close"), Map.of(
                    new PoisonIngredient(mm("<#91fe92>Some rotten flesh"),
                            Material.ROTTEN_FLESH, 3), 0.2f
            ));
            scheduleAfter(() -> lockInteractions(false), TaskSchedule.tick(1));
        }
    }

    private void searchBlock(@NotNull PlayerBlockInteractEvent event, Key sound,
                             @NotNull Map<PoisonIngredient, Float> ingredients) {
        final var player = getPlayer();
        player.playSound(Sound.sound(sound, Sound.Source.MASTER, 1, 1), event.getBlockPosition());

        final var roll = random.nextFloat();
        float sum = 0;
        PoisonIngredient ingredient = null;
        for (Map.Entry<PoisonIngredient, Float> entry : ingredients.entrySet()) {
            if (roll < (entry.getValue() + sum)) {
                ingredient = entry.getKey();
                break;
            }
            sum += entry.getValue();
        }

        if (ingredient != null) {
            if (giveIngredient(ingredient)) {
                player.sendMessage(mm("<#77dd77>You found <ingredient><reset><#77dd77>!",
                        Placeholder.component("ingredient", ingredient.name())));
            } else {
                player.sendMessage(mm("<#ddaa22>You are already holding an item"));
            }
        }
        else player.sendMessage(mm("<#de9390>You didn't find anything!"));
    }

    /**
     * Called when the player clicks the brewing stand to mix the poison
     */
    private void mixPoison(@NotNull CustomPlayer player) {
        if (poison.isMixed()) {
            player.sendMessage(mm("<#bb6699>It seems like this poison was already mixed"));
            return;
        }

        final var mixResult = poison.mix();
        lockInteractions(true);

        scheduleRepeat(() -> {
            poisonDisplay.setVelocity(new Vec(0, 1, 0));
            scheduler.scheduleTask(() -> {
                poisonDisplay.setVelocity(new Vec(0, -1, 0));
            }, TaskSchedule.tick(5), TaskSchedule.stop());

            player.playSound(Sound.sound(Key.key("entity.generic.drink"), Sound.Source.MASTER, 0.5f, 1));
        }, TaskSchedule.tick(10), TaskSchedule.tick(10), 5);

        scheduleAfter(() -> {
            poisonDisplay.setVelocity(Vec.ZERO);
            poisonDisplay.teleport(poisonPos);

            switch (mixResult) {
                case SUCCESS -> {
                    poisonDisplay.setItem(poison.getItem());
                    player.sendPacket(new ParticlePacket(Particle.POOF, poisonPos, Vec.ONE.mul(0.1), 0.02f, 5));
                    lockInteractions(false);
                }
                case EXPLOSION -> {
                    scheduler.scheduleTask(() -> {
                        player.sendPackets(
                                new ParticlePacket(Particle.EXPLOSION_EMITTER, poisonPos, Vec.ONE.mul(5), 0.1f, 10),
                                new ParticlePacket(Particle.ENTITY_EFFECT.withColor(255, poison.getColor()),
                                        poisonPos, Vec.ONE, 5, 50),
                                new DamageEventPacket(player.getEntityId(), new Damage(DamageType.EXPLOSION, null, null, null,0).getTypeId(),
                                        0, 0, poisonPos),
                                new HitAnimationPacket(player.getEntityId(), 90)
                        );
                        player.playSound(Sound.sound(Key.key("entity.generic.explode"), Sound.Source.MASTER, 2, 1));
                        player.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.MASTER, 2, 1.5f));
                        poisonDisplay.setItem(ItemStack.AIR);

                        scheduleAfter(() -> Game.getSceneManager().loseGame(player, "You blew up your garage!"), TaskSchedule.tick(10));
                    }, TaskSchedule.millis(500), TaskSchedule.stop());
                }
            }
        }, TaskSchedule.millis(3500));
    }

    private void entityInteractEvent(@NotNull PlayerEntityInteractEvent event) {
        if (event.getPlayer() != getPlayer() || !isActive()) return;

        if (event.getTarget() instanceof InteractEntity interaction) {
//            log.debug("{} interacted with {}", event.getPlayer(), event.getTarget());

            if (interactionsLocked() && !interaction.doesAllowWhileLocked()) return;

            interaction.interact(event);
        }
    }

    private void interactWithPoison(@NotNull PlayerEntityInteractEvent event) {
        final var player = (CustomPlayer) event.getPlayer();
        final var ingredientItem = player.getItemInMainHand();

        if (ingredientItem != ItemStack.AIR) {
            final var ingredient = new PoisonIngredient(ingredientItem.get(ItemComponent.CUSTOM_NAME),
                    ingredientItem.material(), Objects.requireNonNull(ingredientItem.get(ItemComponent.CUSTOM_DATA))
                    .getTag(Tag.Integer("value")));

            if (!poison.addIngredient(ingredient)) {
                player.sendMessage(mm("<#9999ff>It seems like this ingredient does not fit in the bottle anymore..."));
                player.setItemInMainHand(ItemStack.AIR);
                return;
            }

            poisonDisplay.setItem(poison.getItem());
            player.playSound(Sound.sound(Key.key("item.bottle.fill"), Sound.Source.MASTER,
                    1.0f, 1.0f), new Pos(24.82, 2.60, 0.5, 270, 0));
            player.setItemInMainHand(ItemStack.AIR);
            player.sendPacket(new ParticlePacket(Particle.POOF, poisonPos, Vec.ZERO, 0, ingredient.value()));
        }
    }

    private void interactWithBook(@NotNull PlayerEntityInteractEvent event) {
        final var player = (CustomPlayer) event.getPlayer();
        final var book = PoisonBook.getBook();

        player.openBook(book);
    }

    @Override
    public void start(SceneContext context) {
        super.start(context);

//        final var previousResult = context != null ? context.previousEffect() : null;
        resetPoison();
        lockInteractions(true);

        final var player = getPlayer();

        player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 20, 0));

        player.setRespawnPoint(spawnPos);
        player.teleport(spawnPos);
        scheduler.scheduleTask(() -> {
            player.showTitle(Title.title(mm.deserialize(getVisit()==1 ? "<#99ff99><b>Welcome" : "<#99ff99><b>Welcome back!"),
                    mm(getVisit()==1 ? "<#99ff99>to your garage!": "<#99ff99>Seems like that didn't work! Try again!"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))));
        }, TaskSchedule.seconds(5), TaskSchedule.stop());

        if (getVisit() == 1) {
            scheduler.scheduleTask(() -> {
                player.showTitle(Title.title(mm.deserialize("<#99ff99>Here it is"), mm.deserialize("<#99ff99>you have been working on your plans..."),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofMillis(500))));
            }, TaskSchedule.seconds(10), TaskSchedule.stop());

            scheduler.scheduleTask(() -> {
                player.showTitle(Title.title(Component.empty(), mm.deserialize("<#99ff99>...to poison the president!"),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofSeconds(1))));
            }, TaskSchedule.millis(14500), TaskSchedule.stop());

            scheduler.scheduleTask(() -> {
                player.showTitle(Title.title(Component.empty(), mm.deserialize("<#99ff99>The only problem is..."),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofMillis(500))));
            }, TaskSchedule.seconds(20), TaskSchedule.stop());

            scheduler.scheduleTask(() -> {
                player.showTitle(Title.title(Component.empty(), mm.deserialize("<#99ff99>You have no clue what you are doing!"),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofSeconds(2))));
            }, TaskSchedule.millis(24500), TaskSchedule.stop());

            scheduler.scheduleTask(() -> {
                player.showTitle(Title.title(mm.deserialize("<#99ff99><b>Good luck!"), Component.empty(),
                        Title.Times.times(Duration.ofMillis(1), Duration.ofSeconds(3), Duration.ofSeconds(2))));
                player.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1, 1));
                lockInteractions(false);
            }, TaskSchedule.seconds(30), TaskSchedule.stop());
        } else {
            player.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1, 1));
            scheduleAfter(() -> lockInteractions(false), TaskSchedule.seconds(5));
        }
    }

    @Override
    public void end() {
        super.end();

        resetPoison();
    }

    private void setupIngredients() {
        ingredients.put(new PoisonIngredient(mm("<#dddddd>Expired sheep milk"), Material.MILK_BUCKET, 1), new Pos(28.725, 2.3, 3.5, -180, 0));
        ingredients.put(new PoisonIngredient(mm("<#66ff66>Some weird plant"), Material.GREEN_DYE, 2), new Pos(31.55, 3.65, -2.5, 20, -90));
        ingredients.put(new PoisonIngredient(mm("<#8877ff>A pair of old shoes"), Material.LEATHER_BOOTS, 3), new Pos(26, 3.7, 3.85, 180, -35));
        ingredients.put(new PoisonIngredient(mm("<#66ff00>Bottled nuclear waste"), Material.HONEY_BOTTLE, 8), new Pos(27.6, 4.05, -2.4, 70, -90));
        ingredients.put(new PoisonIngredient(mm("<#5599dd>Toxic mushroom"), Material.BROWN_MUSHROOM, 4), new Pos(23.5, 3.05, 3.4, -130, -90));
    }

    private void spawnEntities() {
        poisonDisplay = new ItemDisplayEntity(poison.getItem());
        poisonDisplay.setScale(Vec.ONE.mul(0.8));
        poisonDisplay.setInstance(getInstance(), poisonPos);
        poisonDisplay.addInteraction(0.6f, 0.6f, this::interactWithPoison);

        final var poisonBook = new ItemDisplayEntity(PoisonBook.getItem());
        poisonBook.setScale(Vec.ONE.mul(0.9));
        poisonBook.setInstance(getInstance(), new Pos(24.3, 2, -0.8, 120, 90));
        poisonBook.addInteraction(0.8f, 0.2f, this::interactWithBook);

        for (Map.Entry<PoisonIngredient, Pos> entry : ingredients.entrySet()) {
            spawnIngredient(entry.getKey(), entry.getValue());
        }
    }

    private void spawnIngredient(@NotNull PoisonIngredient ingredient, Pos pos) {
        final var ingDisplay = new ItemDisplayEntity(ingredient.getItem());
        ingDisplay.setScale(Vec.ONE.mul(0.8));
        ingDisplay.setInstance(getInstance(), pos);

        ingDisplay.addInteraction(0.58f, 0.58f, interaction -> {
            giveIngredient(ingredient);
        });
    }

    private boolean giveIngredient(PoisonIngredient ingredient) {
        if (getPlayer().getInventory().getItemStack(4) == ItemStack.AIR) {
            getPlayer().getInventory().setItemStack(4, ingredient.getItem());
            return true;
        }
        return false;
    }

    public void resetPoison() {
        this.poison = new Poison(seed);
    }
}
