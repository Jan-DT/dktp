package nl.jandt.dktp.scene;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;

public class GarageScene extends BaseScene {
    private static final Pos spawnPos = new Pos(26.5, 1, 0.5, 90, 0);
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();

    private final CustomPlayer player;

    public GarageScene(CustomPlayer player) {
        super("garage-scene");

        this.player = player;
    }

    @Override
    public void start() {
        player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 100, 0));
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 100, 0));
        player.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 100, 0));

        final var poisonStand = new ItemDisplayMeta()

        player.setRespawnPoint(spawnPos);
        player.teleport(spawnPos);
        scheduler.scheduleTask(() -> {
            player.sendActionBar(Component.text("welcome in garage"));
        }, TaskSchedule.seconds(5), TaskSchedule.stop());
    }

    @Override
    public void end() {

    }
}
