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
import net.minestom.server.entity.metadata.villager.VillagerMeta;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import nl.jandt.dktp.entity.HumanEntity;
import nl.jandt.dktp.poison.Poison;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PresidentScene extends BaseScene {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();

    private static final Pos spawnPos = new Pos(0.5, 1.1, 50, 0, 0);
    private static final Pos podiumPos = new Pos(0.5, 2, 69.5, 180, 0);
//    private final LivingEntity president = new LivingEntity(EntityType.VILLAGER);
    private final LivingEntity president = new HumanEntity("President", PlayerSkin.fromUsername("Meshuga"), Component.text("President Poopyhead"));
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
        president.setCustomName(mm.deserialize("<#ffffff>President Poopyhead"));
        president.setCustomNameVisible(true);

        for (Map.Entry<Entity, Pos> entry : npcs.entrySet()) {
            entry.getKey().setInstance(getInstance(), entry.getValue());
        }
    }

    public void presidentSays(Component message) {
        getPlayer().sendMessage(mm.deserialize("<#4444dd><name>: <#ffffff><message>",
                Placeholder.component("name", president.getCustomName()),
                Placeholder.component("message", message)));
    }

    private void presidentDrinks() {
        final var player = getPlayer();

        president.setItemInMainHand(poison.getItem());

        var i = new AtomicInteger();
        scheduler.scheduleTask(() -> {
            scheduler.scheduleTask(() -> {
                player.sendPacket(new EntityAnimationPacket(president.getEntityId(), EntityAnimationPacket.Animation.SWING_MAIN_ARM));
                player.playSound(Sound.sound(Key.key("entity.generic.drink"), Sound.Source.MASTER, 1, 1));

                if (i.incrementAndGet() < 5)
                    return TaskSchedule.tick(5);
                else {
                    triggerPoisonEffect();
                    return TaskSchedule.stop();
                }
            }, TaskSchedule.immediate());
        }, TaskSchedule.millis(500), TaskSchedule.stop());
    }

    private void triggerPoisonEffect() {
        final var player = getPlayer();
        switch (poison.getEffect()) {
            case NO_EFFECT -> {

            }
            case TASTE_WEIRD -> {
            }
            case BECOME_ENTITY -> {
            }
            case FLOAT -> {
            }
            case EXPLODE -> {
            }
            case KILL -> {
            }
        }
    }

    @Override
    public void start() {
        super.start();

        final var player = getPlayer();

        lockInteractions(true);

        player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 20, 0));
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 20, 0));

        player.setRespawnPoint(spawnPos);
        player.teleport(spawnPos);
        scheduler.scheduleTask(() -> {
            player.showTitle(Title.title(mm.deserialize("<#4444dd><b>Press Conference"), mm.deserialize("<#4444dd>Presidential Palace, 2024"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))));
        }, TaskSchedule.seconds(5), TaskSchedule.stop());

        scheduler.scheduleTask(() -> {
            presidentDrinks();
        }, TaskSchedule.seconds(5), TaskSchedule.stop());
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
}
