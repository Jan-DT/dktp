package nl.jandt.dktp.scene;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;

import java.time.Duration;

public class PresidentScene extends BaseScene {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();

    private static final Pos spawnPos = new Pos(0.5, 1, 50, 0, 0);

    public PresidentScene(CustomPlayer player) {
        super("president-scene", player);
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
            player.showTitle(Title.title(mm.deserialize("<#4444dd><b>Press Conference"), mm.deserialize("<#4444dd>Presidential Palace, 2024"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))));
        }, TaskSchedule.seconds(5), TaskSchedule.stop());
    }

    @Override
    public void end() {
        super.end();
    }
}
