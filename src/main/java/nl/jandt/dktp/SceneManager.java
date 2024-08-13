package nl.jandt.dktp;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

import static nl.jandt.dktp.Game.makeInstance;
import static nl.jandt.dktp.scene.animation.Animation.*;

public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    private final Map<CustomPlayer, Map<Class<? extends Scene>, Scene>> playerScenes = new HashMap<>();
    private final Random random = new Random();

    public void switchScene(@NotNull CustomPlayer player, @NotNull Class<? extends Scene> scene, SceneContext context) {
        final @Nullable Scene fromScene = player.getScene();
        final Scene toScene = playerScenes.get(player).get(scene);

        EventDispatcher.callCancellable(new PlayerSwitchSceneEvent(player, fromScene, toScene), () -> {
            if (fromScene != null) {
                log.debug("Player {} exits scene {}", player, fromScene);
                EventDispatcher.call(new PlayerExitSceneEvent(player, fromScene));
            }
            player.setScene(toScene);
            log.debug("Player {} enters scene {}", player, toScene);
            EventDispatcher.call(new PlayerEnterSceneEvent(player, toScene, context));
        });
    }

    public void switchScene(@NotNull CustomPlayer player, @NotNull Class<? extends Scene> scene) {
        switchScene(player, scene, null);
    }

    public void startGame(CustomPlayer player) {
        resetPlayer(player);

        final var seed = random.nextInt();

        playerScenes.compute(player, (k,v) -> {
            final Map<Class<? extends Scene>, Scene> scenes = new HashMap<>();
            scenes.put(GarageScene.class, new GarageScene(player, seed));
            scenes.put(PresidentScene.class, new PresidentScene(player, seed));
            return scenes;
        });

        switchScene(player, GarageScene.class);
    }

    public void resetPlayer(@NotNull CustomPlayer player) {
        player.setInstance(makeInstance(Game.worlds().get("dktp1")), new Pos(0, 0, 0));
        playerScenes.remove(player);
        player.getInventory().clear();
    }

    public void winGame(@NotNull CustomPlayer player) {
        player.teleport(Pos.ZERO);

        scheduleAfter(() -> player.showTitle(Title.title(mm("<#99ff99><b>You won!<b>"), mm("<#99ff99>The president is dead. Now what?"),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2)))), TaskSchedule.seconds(1));

        scheduleAfter(() -> startGame(player), TaskSchedule.seconds(10));
    }

    public void loseGame(@NotNull CustomPlayer player, String loseReason) {
        player.teleport(Pos.ZERO);

        scheduleAfter(() -> player.showTitle(Title.title(mm("<#ff9999><b>You lose!<b>"), mm("<#ff9999><reason>", Placeholder.parsed("reason", loseReason)),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(2)))), TaskSchedule.seconds(1));

        scheduleAfter(() -> startGame(player), TaskSchedule.seconds(10));
    }
}
