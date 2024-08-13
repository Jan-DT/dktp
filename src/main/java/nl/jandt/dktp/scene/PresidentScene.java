package nl.jandt.dktp.scene;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import nl.jandt.dktp.Game;
import nl.jandt.dktp.entity.HumanEntity;
import nl.jandt.dktp.poison.Poison;
import nl.jandt.dktp.scene.animation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static nl.jandt.dktp.scene.animation.Animation.*;

@SuppressWarnings("unused")
public class PresidentScene extends BaseScene {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();

    public static final Pos SPAWN_POS = new Pos(0.5, 1.1, 50, 0, 0);
    public static final Pos PODIUM_POS = new Pos(0.5, 2, 69.5, 180, 0);
    private final HumanEntity president = new HumanEntity("President", PlayerSkin.fromUsername("Meshuga"), mm.deserialize("<#4444dd>President Poopyhead"));

    private final Map<Entity, Pos> npcs = new HashMap<>();

    private Poison poison = new Poison(1);

    public PresidentScene(CustomPlayer player, long seed) {
        super("president-scene", player);

        setupEntities();
        spawnEntities();

        lockInteractions(true);
    }

    private void setupEntities() {
        npcs.putAll(Map.of(
                new Entity(EntityType.VILLAGER), new Pos(4, 1, 60),
                new Entity(EntityType.VILLAGER), new Pos(-2.5, 1, 59.6, -15, 0),
                new Entity(EntityType.VILLAGER), new Pos(6, 1, 62.5, 30, 0),
                new Entity(EntityType.VILLAGER), new Pos(-5, 1, 56.5, -20, 0),
                new Entity(EntityType.VILLAGER), new Pos(-4, 1, 65, -50, 0),
                new Entity(EntityType.VILLAGER), new Pos(5, 1, 65, 50, 0),
                new Entity(EntityType.VILLAGER), new Pos(2.4, 1, 56.6, 7, 0)
        ));
        npcs.putAll(Map.of(
                new Entity(EntityType.IRON_GOLEM), new Pos(5.5, 1, 68.5, 165, 0),
                new Entity(EntityType.IRON_GOLEM), new Pos(-4.5, 1, 68.5, -165, 0),
                new Entity(EntityType.IRON_GOLEM), new Pos(5.5, 1, 50.5, 0, 0),
                new Entity(EntityType.IRON_GOLEM), new Pos(-4.5, 1, 50.5, 0, 0)
        ));
    }

    private void spawnEntities() {
        president.setInstance(getInstance(), PODIUM_POS);

        for (Map.Entry<Entity, Pos> entry : npcs.entrySet()) {
            entry.getKey().setInstance(getInstance(), entry.getValue());
        }
    }

    private void triggerScene() {
        scheduleAfter(() -> {
            final var animation = switch (poison.getEffect()) {
                case NO_EFFECT -> new PresidentNoEffectAnimation(this);
                case EXPLODE -> new PresidentExplodeAnimation(this);
                case FLOAT -> new PresidentFloatAnimation(this);
                case TASTE_WEIRD -> new PresidentTasteWeirdAnimation(this);
                case BECOME_ENTITY -> new PresidentBecomeEntityAnimation(this);
                case KILL -> new PresidentKillAnimation(this);
            };
            final var duration = animation.trigger();

            scheduleAfter(this::endScene, duration);
        }, TaskSchedule.seconds(10));
    }

    private void endScene() {
        if (poison.getEffect().isKill()) Game.getSceneManager().winGame(getPlayer());
        else if (getVisit() > 2) Game.getSceneManager().loseGame(getPlayer(), "You did not manage to kill the president!");
        else Game.getSceneManager().switchScene(getPlayer(), GarageScene.class, new SceneContext(null, poison.getEffect()));
    }

    @Override
    public void start(SceneContext context) {
        super.start(context);

        if (context != null) this.poison = context.poison();

        final var player = getPlayer();
        player.getInventory().clear();

        player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 100, 0));
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 100, 0));
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 100, 0));

        player.setRespawnPoint(SPAWN_POS);
        player.teleport(SPAWN_POS);
        scheduleAfter(() -> {
            player.showTitle(Title.title(mm.deserialize("<#4444dd><b>Press Conference"), mm.deserialize("<#4444dd>Presidential Palace, 2024"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))));
        }, TaskSchedule.seconds(5));

        scheduleAfter(this::triggerScene, TaskSchedule.seconds(5));
    }

    @Override
    public void end() {
        super.end();
    }

    public Poison getPoison() {
        return poison;
    }

    public void setPoison(Poison poison) {
        this.poison = poison;
    }

    public HumanEntity getPresident() {
        return president;
    }

    public Map<Entity, Pos> getNpcs() {
        return npcs;
    }

    public Entity randomNpc() {
        return npcs.keySet().stream().toList().get(new Random().nextInt(0, npcs.size()-1));
    }
}
