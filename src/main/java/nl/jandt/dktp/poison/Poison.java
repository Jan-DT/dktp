package nl.jandt.dktp.poison;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.component.DataComponentMap;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.PotionType;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Poison {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private final long seed;
    private final List<PoisonIngredient> ingredients = new ArrayList<>();
    private int ingredientSum = 0;
    private ItemStack item;
    private PoisonEffect poisonEffect = PoisonEffect.NO_EFFECT;

    public Poison(long seed) {
        this.seed = seed;
        generateItem(TextColor.color(56, 56, 198));
    }

    public boolean hasIngredient(PoisonIngredient ingredient) {
        return ingredients.contains(ingredient);
    }

    public boolean hasIngredient(@NotNull ItemStack ingredientItem) {
        final var itemValue = ingredientItem.getTag(Tag.Integer("value"));
        return ingredients.stream().anyMatch(ing -> ing.value() == itemValue);
    }

    public void addIngredient(PoisonIngredient ingredient) {
        if (ingredients.contains(ingredient)) return;

        ingredients.add(ingredient);
        ingredientSum += ingredient.value();
        generateItem(randomColor(new Random(seed + ingredientSum)));
    }

    public List<PoisonIngredient> getIngredients() {
        return ingredients;
    }

    public void generateItem(RGBLike color) {
        final var itemName = mm.deserialize("<#bb66ff>The Poison");
        item = ItemStack.of(Material.POTION, DataComponentMap.EMPTY
                        .set(ItemComponent.ITEM_NAME, itemName)
                        .set(ItemComponent.CUSTOM_NAME, mm.deserialize("<reset><#bbbbbb>You are holding <item_name>",
                                Placeholder.component("item_name", itemName)))
                        .set(ItemComponent.POTION_CONTENTS, new PotionContents(PotionType.AWKWARD, color)));
    }

    public ItemStack getItem() {
        return item;
    }

    @NotNull TextColor randomColor(@NotNull Random random) {
        return TextColor.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public MixResult mix() {
        final var random = new Random(seed + ingredientSum);
        generateItem(randomColor(random));
        return MixResult.roll(random);
    }

    public enum MixResult {
        SUCCESS,
        EXPLOSION;

        static MixResult roll(@NotNull Random random) {
            final var res = random.nextFloat();
            if (res > 0.2) return MixResult.SUCCESS;
            else return MixResult.EXPLOSION;
        }
    }

    public enum PoisonEffect {
        NO_EFFECT,
        TASTE_WEIRD,
        BECOME_ENTITY,
        EXPLODE,
        KILL;

        public boolean isSuspicious() {
            return switch (this) {
                case NO_EFFECT, TASTE_WEIRD -> false;
                default -> true;
            };
        }

        public boolean isKill() {
            return switch (this) {
                case EXPLODE, KILL -> true;
                default -> false;
            };
        }
    }
}
