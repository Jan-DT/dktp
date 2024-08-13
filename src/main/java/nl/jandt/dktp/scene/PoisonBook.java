package nl.jandt.dktp.scene;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static nl.jandt.dktp.scene.animation.Animation.mm;

public class PoisonBook {
    private static final Component name = mm("<#dd8844>Gentlemen's Guide to Poisoning");

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static @NotNull Book getBook() {
        final Collection<Component> pages = List.of(
                mm("<br><br><b> Gentlemen's Guide<br>    to Poisoning</b><br><br><br><br><br><br><br><br>   Charles Gentlemen<br><br>           1835"),
                mm(""),
                mm("The art of making a beautiful poison is a fine one. <br><br>" +
                        "It is one that not many can master, especially the skill of creating a poison as potent as mine. <br><br>" +
                        "It requires willpower, care and vigor that not all can enjoy."),
                mm("You might be wondering... Who is this Gentlemen gentleman?<br><br>" +
                        "Well, I can assert to you that I am responsible for the poisoning of famous philosophers, " +
                        "rulers and kings to the likes of Socrates, Napoleon Bonaparte and Grigori Rasputin."),
                mm("Now, the most important thing to know about mixing poison is that it takes patience.<br>" +
                        "It is absolutely not to be done in a rush, as you might just blow yourself up.<br>" +
                        "Just like classical ballet dancing, it takes gentle care and practice."),
                mm("For one, y\u2591\u2591 shall not throw rand\u2591m ingredients together. " +
                        "You might not kno\u2591\u2591\u2592\u2592\u2591\u2591 will happen on con\u2591\u2592\u2592\u2592\u2591tion.<br>" +
                        "Also, care\u2591\u2591ss mixing might \u2591\u2591\u2592\u2592\u2591\u2592\u2591ow off your hat. " +
                        "And you c\u2591\u2591\u2592\u2592\u2591\u2591t aff\u2591rd that when trying to remain stealthy."),
                mm("Now, to star\u2591 off, we ne\u2591d some \u2591\u2592\u2592\u2592\u2592\u2592\u2591\u2591\u2591. This is very important, " +
                        "as to prevent u\u2591\u2591\u2592\u2592\u2591pec\u2591\u2591\u2592\u2592\u2592\u2592\u2591ults. Besides that, you al\u2591\u2592\u2592\u2592ed \u2591\u2592\u2592\u2591\u2591\u2591\u2591.<br>" +
                        "Once this all i\u2591\u2591\u2592\u2592\u2592\u2592\u2591\u2591\u2591\u2591\u2591 the poison, it nee\u2591\u2591\u2592\u2592\u2592c\u2591reful mixing."),
                mm("Mixing too mu\u2591\u2591\u2592\u2592\u2591ight lead\u2591\u2591o\u2592\u2591\u2591\u2592\u2592gero\u2591\u2591\u2591\u2592\u2592\u2591uations." +
                        "Now <br>"+"\u2593".repeat(150)),
                mm("\u2593".repeat(250)),
                mm("\u2593".repeat(250)),
                mm("\u2593".repeat(250)),
                mm("\u2593".repeat(250))
        );

        return Book.book(name, Component.text("Charles Gentlemen"), pages);
    }

    @Contract(" -> new")
    public static @NotNull ItemStack getItem() {
        return ItemStack.of(Material.BOOK)
                .with(ItemComponent.ITEM_NAME, name);
    }
}
