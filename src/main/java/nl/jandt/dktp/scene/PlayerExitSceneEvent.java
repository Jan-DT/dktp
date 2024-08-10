package nl.jandt.dktp.scene;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerExitSceneEvent implements PlayerEvent {
    private Player player;
    private Scene scene;

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