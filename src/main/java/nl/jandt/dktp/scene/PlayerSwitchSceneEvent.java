package nl.jandt.dktp.scene;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PlayerSwitchSceneEvent implements PlayerEvent, CancellableEvent {
    private final Player player;
    private final Scene fromScene;
    private final Scene toScene;
    private boolean cancelled = false;

    public PlayerSwitchSceneEvent(Player player, Scene fromScene, Scene toScene) {
        this.player = player;
        this.fromScene = fromScene;
        this.toScene = toScene;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Scene getOldScene() {
        return fromScene;
    }

    public @NotNull Scene getNewScene() {
        return toScene;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
