package nl.jandt.dktp.scene;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PlayerExitSceneEvent implements PlayerEvent {
    private final Player player;
    private final Scene scene;

    public PlayerExitSceneEvent(Player player, Scene scene) {
        this.player = player;
        this.scene = scene;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public Scene getScene() {
        return scene;
    }
}
