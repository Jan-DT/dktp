package nl.jandt.dktp;

import net.minestom.server.event.EventDispatcher;
import nl.jandt.dktp.scene.PlayerEnterSceneEvent;
import nl.jandt.dktp.scene.PlayerExitSceneEvent;
import nl.jandt.dktp.scene.PlayerSwitchSceneEvent;
import nl.jandt.dktp.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    public void switchScene(@NotNull CustomPlayer player, @NotNull Scene toScene) {
        final @Nullable Scene fromScene = player.getScene();

        EventDispatcher.callCancellable(new PlayerSwitchSceneEvent(player, fromScene, toScene), () -> {
            if (fromScene != null) {
                log.debug("Player {} exits scene {}", player, fromScene);
                EventDispatcher.call(new PlayerExitSceneEvent(player, fromScene));
            }
            player.setScene(toScene);
            log.debug("Player {} enters scene {}", player, toScene);
            EventDispatcher.call(new PlayerEnterSceneEvent(player, toScene));
        });
    }
}
