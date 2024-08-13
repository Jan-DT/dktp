package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.BaseScene;
import nl.jandt.dktp.scene.PresidentScene;

import static nl.jandt.dktp.scene.animation.Animation.*;

public abstract class PresidentAnimation implements Animation {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    protected final nl.jandt.dktp.scene.PresidentScene scene;

    public PresidentAnimation(PresidentScene scene) {
        this.scene = scene;
    }

    protected void sayAfter(Component message, TaskSchedule delay) {
        scheduleAfter(() -> presidentSays(message), delay);
    }

    protected void respondAfter(Component message, TaskSchedule delay) {
        scheduleAfter(() -> journalistResponds(message), delay);
    }

    protected void sayAfter(String message, int seconds) {
        sayAfter(mm(message), TaskSchedule.seconds(seconds));
    }

    protected void respondAfter(String message, int seconds) {
        respondAfter(mm(message), TaskSchedule.seconds(seconds));
    }

    protected void presidentSays(Component message) {
        scene.getPlayer().playSound(Sound.sound(Key.key("entity.pillager.ambient"), Sound.Source.MASTER, 1, 1));
        scene.getPlayer().sendMessage(mm.deserialize("<#4444dd>Pres. Poopyhead: <#ffffff><message>",
                Placeholder.component("message", message)));
    }

    protected void journalistResponds(Component message) {
        scene.getPlayer().playSound(Sound.sound(Key.key("entity.villager.ambient"), Sound.Source.MASTER, 1, 1));
        scene.getPlayer().sendMessage(mm.deserialize("<#ddff88>Journalist: <#ffffff><message>",
                Placeholder.component("message", message)));
    }

    protected void presidentTakesSip() {
        scene.getPresident().setItemInMainHand(scene.getPoison().getItem());
        scheduleRepeat(()-> {
            scene.getPlayer().sendPacket(new EntityAnimationPacket(scene.getPresident().getEntityId(), EntityAnimationPacket.Animation.SWING_MAIN_ARM));
            scene.getPlayer().playSound(Sound.sound(Key.key("entity.generic.drink"), Sound.Source.MASTER, 1, 1));
        }, TaskSchedule.immediate(), TaskSchedule.tick(5), 10);
        scheduleAfter(() -> scene.getPresident().setItemInMainHand(ItemStack.AIR), TaskSchedule.seconds(3));
    }

    protected void peopleCelebrate() {
        scheduleRepeat(() -> scene.getPlayer().playSound(Sound.sound(Key.key("entity.villager.celebrate"), Sound.Source.MASTER, 1, 1),
                        scene.randomNpc().getPosition()),
                TaskSchedule.immediate(), TaskSchedule.tick(7), 10);
    }
}
