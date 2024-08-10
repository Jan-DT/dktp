package nl.jandt.dktp.poison;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.item.Material;

public class PoisonIngredient {
    private final Material material;
    private final Component name;
    private final Component lore;

    public PoisonIngredient(Component name, Material material, Component lore) {
        this.material = material;
        this.name = name;
        this.lore = lore;
    }

    public PoisonIngredient(Component name, Material material) {
        this(name, material, Component.text("I have no clue what this does...")
                .color(TextColor.color(170,170,170)));
    }
}
