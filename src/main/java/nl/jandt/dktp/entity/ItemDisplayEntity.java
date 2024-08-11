package nl.jandt.dktp.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class ItemDisplayEntity extends StaticEntity {

    private ItemDisplayEntity() {
        super(EntityType.ITEM_DISPLAY);
    }

    public ItemDisplayEntity(ItemStack itemStack) {
        this();

        getEntityMeta().setItemStack(itemStack);
        getEntityMeta().setDisplayContext(ItemDisplayMeta.DisplayContext.FIXED);
    }

    public @NotNull ItemDisplayMeta getEntityMeta() {
        return (ItemDisplayMeta) super.getEntityMeta();
    }

    public InteractEntity addInteraction(float width, float height, Pos pos, Consumer<PlayerEntityInteractEvent> consumer) {
        final var interaction = new InteractEntity(width, height);
        interaction.setInstance(getInstance(), pos);
        interaction.setConsumer(consumer);
        interaction.setTag(Tag.Component("Tooltip"), getItem().get(ItemComponent.ITEM_NAME));
        return interaction;
    }

    public InteractEntity addInteraction(float width, float height, Consumer<PlayerEntityInteractEvent> consumer) {
        return addInteraction(width, height, getPosition().sub(0, height*1.2/2, 0), consumer);
    }

    public InteractEntity addInteraction(float width, float height) {
        return addInteraction(width, height, null);
    }

    public void setItem(ItemStack itemStack) {
        getEntityMeta().setItemStack(itemStack);
    }

    public ItemStack getItem() {
        return getEntityMeta().getItemStack();
    }

    public void setScale(Vec value) {
        getEntityMeta().setScale(value);
    }

    public void setDisplayContext(ItemDisplayMeta.DisplayContext context) {
        getEntityMeta().setDisplayContext(context);
    }
}
