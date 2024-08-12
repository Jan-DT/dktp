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

public class PoisonBook {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Component name = mm.deserialize("<#dd8844>Gentlemen's Guide to Poisoning");

    public static @NotNull Book getBook() {
        // TODO: add more pages
        final Collection<Component> pages = List.of(
                mm.deserialize("<br><br><b> Gentlemen's Guide<br>    to Poisoning</b><br><br><br><br><br><br><br><br>   Charles Gentlemen<br><br>           1835"),
                mm.deserialize(""),
                mm.deserialize("The art of making a beautiful poison is a fine one. <br><br>" +
                        "It is one that not many can master, especially the skill of creating a poison as potent as mine. <br><br>" +
                        "It requires willpower, care and vigor that not all can enjoy."),
                mm.deserialize(
                        "You might be wondering... Who is this Gentlemen gentleman?<br><br>" +
                        "Well, I can assert to you that I am responsible for the poisoning of famous philosophers, " +
                        "rulers and kings to the likes of Socrates, Napoleon Bonaparte and Grigori Rasputin.")
        );

        return Book.book(name, Component.text("Charles Gentlemen"), pages);
    }

    @Contract(" -> new")
    public static @NotNull ItemStack getItem() {
        return ItemStack.of(Material.BOOK)
                .with(ItemComponent.ITEM_NAME, name);
    }
}
