package nl.jandt.dktp.scene;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import nl.jandt.dktp.entity.InteractEntity;
import nl.jandt.dktp.entity.ItemDisplayEntity;
import nl.jandt.dktp.poison.Poison;
import nl.jandt.dktp.poison.PoisonIngredient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


public class GarageScene extends BaseScene {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private static final Pos spawnPos = new Pos(26.5, 1, 0.5, 90, 0);
    private static final Logger log = LoggerFactory.getLogger(GarageScene.class);
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();
    private final int seed;

    private final List<Entity> entities = new ArrayList<>();
//    private final Map<Entity, Consumer<PlayerEntityInteractEvent>> interactions = new HashMap<>();
    private final Map<PoisonIngredient, Pos> ingredients = new HashMap<>();
    private Poison poison;
    private ItemDisplayEntity poisonDisplay;

    public GarageScene(CustomPlayer player, int seed) {
        super("garage-scene", player);

        this.seed = seed;

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

        if (losEntity != null && losEntity.hasTag(Tag.Component("Tooltip"))) {
            tooltip = losEntity.getTag(Tag.Component("Tooltip"));
        }

        final var losPoint = player.getTargetBlockPosition(3);
        if (losPoint != null && getInstance().getBlock(losPoint) != Block.AIR) {
            final var losBlock = getInstance().getBlock(losPoint);

            if (losBlock.compare(Block.IRON_TRAPDOOR)) tooltip = mm.deserialize("<#ffbbbb><b>Try your poison</b> \uD83E\uDC9A");
        }

        if (tooltip != null) player.sendActionBar(tooltip);
        else player.sendActionBar(Component.empty());
    }

    private void blockInteractEvent(@NotNull PlayerBlockInteractEvent event) {
        if (event.getPlayer() != getPlayer() || !isActive()) return;

        final var player = (CustomPlayer) event.getPlayer();

        if (event.getBlock().compare(Block.IRON_TRAPDOOR)) getPlayer().sendMessage("test");
        else if (event.getBlock().compare(Block.CHEST)) {
            player.sendPacket(new BlockActionPacket(event.getBlockPosition(), (byte) 1, (byte) 1, Block.CHEST));
            scheduler.scheduleTask(() -> player.sendPacket(new BlockActionPacket(event.getBlockPosition(), (byte) 1, (byte) 0, Block.CHEST)),
                    TaskSchedule.seconds(5), TaskSchedule.stop());
        }
    }

    private void entityInteractEvent(@NotNull PlayerEntityInteractEvent event) {
        if (event.getPlayer() != getPlayer() || !isActive()) return;

        if (event.getTarget() instanceof InteractEntity interaction) {
            log.debug("{} interacted with {}", event.getPlayer(), event.getTarget());
            interaction.interact(event);
        }
    }

    private void interactWithPoison(@NotNull PlayerEntityInteractEvent event) {
        final var player = (CustomPlayer) event.getPlayer();
        final var ingredient = player.getItemInMainHand();

        if (ingredient != ItemStack.AIR) {
            if (poison.hasIngredient(ingredient)) {
                player.sendMessage(mm.deserialize("<#9999ff>It seems like this ingredient is already in your mixture..."));
                player.setItemInMainHand(ItemStack.AIR);
                return;
            }

            poison.addIngredient(new PoisonIngredient(ingredient.get(ItemComponent.CUSTOM_NAME),
                    ingredient.material(), Objects.requireNonNull(ingredient.get(ItemComponent.CUSTOM_DATA))
                        .getTag(Tag.Integer("value"))));
            poisonDisplay.setItem(poison.getItem());
            player.playSound(Sound.sound(Key.key("item.bottle.fill"), Sound.Source.MASTER,
                    1.0f, 1.0f), new Pos(24.82, 2.60, 0.5, 270, 0));
            player.setItemInMainHand(ItemStack.AIR);
        }
    }

    private void interactWithBook(@NotNull PlayerEntityInteractEvent event) {
        final var player = (CustomPlayer) event.getPlayer();
        final var book = PoisonBook.getBook();

        player.openBook(book);
    }

    @Override
    public void start() {
        super.start();

        final var player = getPlayer();

        player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 20, 0));

        player.setRespawnPoint(spawnPos);
        player.teleport(spawnPos);
        scheduler.scheduleTask(() -> {
            player.showTitle(Title.title(mm.deserialize(getVisit()==1 ? "<#99ff99><b>Welcome" : "<#99ff99><b>Welcome back"), mm.deserialize("<#99ff99>to your garage!"),
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
            }, TaskSchedule.seconds(30), TaskSchedule.stop());
        }
    }

    @Override
    public void end() {
        super.end();

        resetPoison();
    }

    private void setupIngredients() {
        ingredients.put(new PoisonIngredient(mm.deserialize("<#66ff66>Some weird plant"), Material.GREEN_DYE, 1), new Pos(28.725, 2.3, 3.5, -180, 0));
    }

    private void spawnEntities() {
        poisonDisplay = new ItemDisplayEntity(poison.getItem());
        poisonDisplay.setScale(Vec.ONE.mul(0.8));
        poisonDisplay.setInstance(getInstance(), new Pos(24.82, 2.60, 0.5, 270, 0));
        entities.add(poisonDisplay);
        entities.add(poisonDisplay.addInteraction(0.6f, 0.6f, this::interactWithPoison));

        final var poisonBook = new ItemDisplayEntity(PoisonBook.getItem());
        poisonBook.setScale(Vec.ONE.mul(0.9));
        poisonBook.setInstance(getInstance(), new Pos(24.3, 2, -0.8, 120, 90));
        entities.add(poisonBook);
        entities.add(poisonBook.addInteraction(0.8f, 0.2f, this::interactWithBook));

        for (Map.Entry<PoisonIngredient, Pos> entry : ingredients.entrySet()) {
            spawnIngredient(entry.getKey(), entry.getValue());
        }
    }

    private void spawnIngredient(@NotNull PoisonIngredient ingredient, Pos pos) {
        final var ingDisplay = new ItemDisplayEntity(ingredient.getItem());
        ingDisplay.setScale(Vec.ONE.mul(0.8));
        ingDisplay.setInstance(getInstance(), pos);
        entities.add(ingDisplay);

        ingDisplay.addInteraction(0.6f, 0.6f, interaction -> {
            final var inventory = interaction.getPlayer().getInventory();

            if (inventory.getItemStack(4) == ItemStack.AIR)
                inventory.setItemStack(4, ingredient.getItem());
        });
    }

    public void resetPoison() {
        this.poison = new Poison(seed);
    }
}
