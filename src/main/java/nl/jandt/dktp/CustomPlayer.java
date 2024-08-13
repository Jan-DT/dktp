package nl.jandt.dktp;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.player.PlayerConnection;
import nl.jandt.dktp.scene.Scene;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class CustomPlayer extends Player {
    private Scene scene;

    public CustomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public @Nullable Entity getBlockedLosEntity(double range, Predicate<Entity> predicate) {
        Instance instance = getInstance();
        if (instance == null) {
            return null;
        }

        final Pos start = position.withY(position.y() + getEyeHeight());
        final Vec startAsVec = start.asVec();
        final Predicate<Entity> finalPredicate = e -> e != this
                && e.getBoundingBox().boundingBoxRayIntersectionCheck(startAsVec, position.direction(), e.getPosition())
                && predicate.test(e);

        Optional<Entity> nearby = instance.getNearbyEntities(position, range).stream()
                .filter(finalPredicate)
                .min(Comparator.comparingDouble(e -> e.getDistanceSquared(this)));

        return nearby.orElse(null);
    }
}
