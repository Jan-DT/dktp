package nl.jandt.dktp.poison;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static nl.jandt.dktp.scene.animation.Animation.mm;

@SuppressWarnings("unused")
public class Poison {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Logger log = LoggerFactory.getLogger(Poison.class);
    private static final int maxIngredients = 6;

    private final long seed;
    private final PoisonEffect poisonEffect = PoisonEffect.NO_EFFECT;
    private final List<PoisonIngredient> ingredients = new ArrayList<>();
    private int ingredientSum = 0;
    private boolean mixed = false;
    private ItemStack item;
    private RGBLike color = TextColor.color(56, 56, 198);

    public Poison(long seed) {
        this.seed = seed;
        generateItem();
    }

    public boolean hasIngredient(PoisonIngredient ingredient) {
        return ingredients.contains(ingredient);
    }

    public boolean hasIngredient(@NotNull ItemStack ingredientItem) {
        final var itemValue = ingredientItem.getTag(Tag.Integer("value"));
        return ingredients.stream().anyMatch(ing -> ing.value() == itemValue);
    }

    public boolean addIngredient(PoisonIngredient ingredient) {
        if (ingredients.size() > maxIngredients) return false;
        mixed = false;

        ingredients.add(ingredient);
        ingredientSum += ingredient.value();

        color = randomColor(new Random(seed + ingredientSum));
        generateItem();
        return true;
    }

    public List<PoisonIngredient> getIngredients() {
        return ingredients;
    }

    protected void generateItem(RGBLike color) {
        final var itemName = mm("<#bb66ff>The Poison");
        item = ItemStack.of(Material.POTION, DataComponentMap.EMPTY
                        .set(ItemComponent.ITEM_NAME, itemName)
                        .set(ItemComponent.CUSTOM_NAME, mm("<reset><#bbbbbb>You are holding <item_name>",
                                Placeholder.component("item_name", itemName)))
                        .set(ItemComponent.POTION_CONTENTS, new PotionContents(PotionType.AWKWARD, color)));
    }

    protected void generateItem() {
        generateItem(color);
    }

    public ItemStack getItem() {
        return item;
    }

    public int getIngredientSum() {
        return ingredientSum;
    }

    @NotNull TextColor randomColor(@NotNull Random random) {
        return TextColor.color(random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    public MixResult mix() {
        if (ingredientSum > 2) {
            ingredientSum += 1;
        }

        final var random = new Random(seed + ingredientSum);
        if (ingredientSum > 0) color = randomColor(random);
        generateItem();
        mixed = true;
        return MixResult.roll(ingredientSum, random);
    }

    public PoisonEffect getEffect() {
        return PoisonEffect.roll(ingredientSum, seed);
    }

    public RGBLike getColor() {
        return color;
    }

    public boolean isMixed() {
        return mixed;
    }

    public enum MixResult {
        SUCCESS,
        EXPLOSION;

        static MixResult roll(long ingredientSum, Random random) {
            if (ingredientSum > 3) {
                final var res = random.nextFloat();
                if (res > 0.2) return MixResult.SUCCESS;
                else return MixResult.EXPLOSION;
            } else {
                return MixResult.SUCCESS;
            }
        }
    }

    public enum PoisonEffect {
        NO_EFFECT,
        TASTE_WEIRD,
        BECOME_ENTITY,
        FLOAT,
        EXPLODE,
        KILL;

        public static @NotNull PoisonEffect roll(long ingredientSum, long seed) {
            if (ingredientSum == 0) {
                return PoisonEffect.NO_EFFECT;
            } else if (ingredientSum < 2) {
                return PoisonEffect.TASTE_WEIRD;
            } else {
                final var random = new Random(seed + ingredientSum);
                final var effectList = List.of(
                        PoisonEffect.FLOAT, PoisonEffect.BECOME_ENTITY,
                        PoisonEffect.EXPLODE, PoisonEffect.KILL
                );

                try {
                    return effectList.get(random.nextInt(0, effectList.size()));
                } catch (IndexOutOfBoundsException e) {
                    log.warn("Attempt to index incorrect effect: {}", e.toString());
                    return roll(ingredientSum, seed);
                }
            }
        }

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
