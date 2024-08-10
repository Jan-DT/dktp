package nl.jandt.dktp;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import nl.jandt.dktp.scene.Scene;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomPlayer extends Player {
    private Scene scene;

    public CustomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public Scene getScene() {
        return scene;
    }

    @ApiStatus.Internal
    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
