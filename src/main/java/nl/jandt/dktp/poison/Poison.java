package nl.jandt.dktp.poison;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.component.DataComponentMap;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Poison {
    private final Random random;

    private final List<PoisonIngredient> ingredients = new ArrayList<>();
    private ItemStack item;

    public Poison(long seed) {
        random = new Random(seed);
        generateItem(TextColor.color(255, 255, 255));
    }

    public void addIngredient(PoisonIngredient ingredient) {
        ingredients.add(ingredient);
        generateItem(randomColor());
    }

    public void generateItem(RGBLike color) {
        item = ItemStack.of(Material.POTION,
                DataComponentMap.EMPTY.set(ItemComponent.POTION_CONTENTS, new PotionContents(PotionType.AWKWARD, color)));
    }

    public ItemStack getItem() {
        return item;
    }

    @NotNull TextColor randomColor() {
        return TextColor.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public MixResult mix() {
        generateItem(randomColor());
        return MixResult.roll(random);
    }

    public enum MixResult {
        SUCCESS,
        EXPLOSION;

        static MixResult roll(Random random) {
            final var res = random.nextFloat();
            if (res > 0.2) return MixResult.SUCCESS;
            else return MixResult.EXPLOSION;
        }
    }
}
