package nl.jandt.dktp.scene;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class ItemDisplayEntity extends Entity {
    public ItemDisplayEntity(@NotNull EntityType entityType,
                             @NotNull UUID uuid) {
        super(EntityType.ITEM_DISPLAY, uuid);

        this.hasPhysics = false;
        this.hasCollision = true;

        setNoGravity(true);
    }

    public ItemDisplayEntity(@NotNull EntityType entityType) {
        super(entityType, UUID.randomUUID());
    }

    public void updateItem(ItemStack itemStack) {
        final var display = (ItemDisplayMeta) getEntityMeta();

        display.setItemStack(itemStack);
    }
}
