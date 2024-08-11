package nl.jandt.dktp.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class StaticEntity extends Entity {
    public StaticEntity(@NotNull EntityType entityType) {
        super(entityType);

        this.hasPhysics = false;
        this.hasCollision = false;

        setNoGravity(true);
    }
}
