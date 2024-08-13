package nl.jandt.dktp.scene;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PlayerEnterSceneEvent implements PlayerEvent {
    private final Player player;
    private final Scene scene;
    private final @Nullable SceneContext context;

    public PlayerEnterSceneEvent(Player player, Scene scene, SceneContext context) {
        this.player = player;
        this.scene = scene;
        this.context = context;
    }

    public PlayerEnterSceneEvent(Player player, Scene scene) {
        this(player, scene, null);
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public Scene getScene() {
        return scene;
    }

    public @Nullable SceneContext getContext() {
        return context;
    }
}
