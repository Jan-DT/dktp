package nl.jandt.dktp.entity;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class InteractEntity extends StaticEntity {
    private static final Logger log = LoggerFactory.getLogger(InteractEntity.class);
    private Consumer<PlayerEntityInteractEvent> consumer;

    public InteractEntity() {
        super(EntityType.INTERACTION);
    }

    public InteractEntity(float width, float height) {
        this();

        setWidth(width);
        setHeight(height);
    }

    public InteractEntity(float width, float height, Consumer<PlayerEntityInteractEvent> consumer) {
        this(width, height);

        setConsumer(consumer);
    }

    @Override
    public @NotNull InteractionMeta getEntityMeta() {
        return (InteractionMeta) super.getEntityMeta();
    }

    public void setConsumer(@NotNull Consumer<PlayerEntityInteractEvent> consumer) {
        this.consumer = consumer;

        eventNode().addListener(PlayerEntityInteractEvent.class, consumer);
    }

    public void interact(PlayerEntityInteractEvent interaction) {
        if (consumer == null) {
            log.warn("Attempted interaction with consumer-less interaction!");
            return;
        }
        this.consumer.accept(interaction);
    }

    public void setWidth(float width) {
        getEntityMeta().setWidth(width);
        updateBoundingBox();
    }

    public float getWidth() {
        return getEntityMeta().getWidth();
    }

    public void setHeight(float height) {
        getEntityMeta().setHeight(height);
        updateBoundingBox();
    }

    public float getHeight() {
        return getEntityMeta().getHeight();
    }

    public void updateBoundingBox() {
        setBoundingBox(getWidth(), getHeight(), getWidth());
    }
}
