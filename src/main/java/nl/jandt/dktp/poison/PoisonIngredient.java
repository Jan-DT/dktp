package nl.jandt.dktp.poison;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.component.DataComponentMap;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PoisonIngredient(Component name, Material material, List<Component> lore, int value) {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public PoisonIngredient(Component name, Material material, int value) {
        this(name, material, List.of(Component.text("You have no clue what this does...")
                .color(TextColor.color(170,170,170))), value);
    }

    @Contract(" -> new")
    public @NotNull ItemStack getItem() {
        return ItemStack.of(material, DataComponentMap.EMPTY
                .set(ItemComponent.ITEM_NAME, name)
                .set(ItemComponent.CUSTOM_NAME, mm.deserialize("<reset><#bbbbbb>You are holding <item_name>",
                        Placeholder.component("item_name", name)))
                .set(ItemComponent.LORE, lore)
                .set(ItemComponent.CUSTOM_DATA, CustomData.EMPTY.withTag(Tag.Integer("value"), value)));
    }
}
