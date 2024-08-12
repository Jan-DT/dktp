package nl.jandt.dktp.scene;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import nl.jandt.dktp.entity.HumanEntity;
import nl.jandt.dktp.poison.Poison;
import nl.jandt.dktp.scene.animation.PresidentBecomeEntityAnimation;
import nl.jandt.dktp.scene.animation.PresidentExplodeAnimation;
import nl.jandt.dktp.scene.animation.PresidentFloatAnimation;
import nl.jandt.dktp.scene.animation.PresidentNoEffectAnimation;

import java.time.Duration;
import java.util.Map;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentScene extends BaseScene {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();

    private static final Pos spawnPos = new Pos(0.5, 1.1, 50, 0, 0);
    private static final Pos podiumPos = new Pos(0.5, 2, 69.5, 180, 0);
    private final HumanEntity president = new HumanEntity("President", PlayerSkin.fromUsername("Meshuga"), mm.deserialize("<#4444dd>President Poopyhead"));
    private final Map<Entity, Pos> npcs = Map.of(
            new Entity(EntityType.VILLAGER), new Pos(4, 1, 60),

            new Entity(EntityType.IRON_GOLEM), new Pos(5.5, 1, 68.5, 165, 0),
            new Entity(EntityType.IRON_GOLEM), new Pos(-4.5, 1, 68.5, -165, 0),
            new Entity(EntityType.IRON_GOLEM), new Pos(5.5, 1, 50.5, 0, 0),
            new Entity(EntityType.IRON_GOLEM), new Pos(-4.5, 1, 50.5, 0, 0)
    );

    private Poison poison = new Poison(1);

    public PresidentScene(CustomPlayer player) {
        super("president-scene", player);

        spawnEntities();
    }

    private void spawnEntities() {
        president.setInstance(getInstance(), podiumPos);

        for (Map.Entry<Entity, Pos> entry : npcs.entrySet()) {
            entry.getKey().setInstance(getInstance(), entry.getValue());
        }
    }

    private void triggerScene() {
        scheduleAfter(() -> {
            var animation = switch (poison.getEffect()) {
                case NO_EFFECT -> new PresidentNoEffectAnimation();
                case EXPLODE -> new PresidentExplodeAnimation();
                case FLOAT -> new PresidentFloatAnimation();
                case BECOME_ENTITY -> new PresidentBecomeEntityAnimation();
                default -> new PresidentNoEffectAnimation();
            };
            animation = new PresidentBecomeEntityAnimation();
            animation.trigger(this);
        }, TaskSchedule.millis(500));
    }

    @Override
    public void start() {
        super.start();

        lockInteractions(true);

        final var player = getPlayer();

        player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 20, 0));

        player.setRespawnPoint(spawnPos);
        player.teleport(spawnPos);
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
}
