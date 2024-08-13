package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.network.packet.server.play.DamageEventPacket;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentKillAnimation extends PresidentAnimation {
    public PresidentKillAnimation(PresidentScene scene) {
        super(scene);
    }

    @Override
    public TaskSchedule trigger() {
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        sayAfter("Hi everyone.", 1);
        sayAfter("I have gathered you here for one reason", 5);
        sayAfter("I want to announce that I am banning all good music.", 10);
        sayAfter("Artists will include, but are not limited to:", 15);
        sayAfter("C418,", 16);
        sayAfter("System of a Down,", 17);
        sayAfter("Metallica,", 18);
        sayAfter("Pink Floyd,", 19);
        sayAfter("Radiohead,", 20);
        sayAfter("Vulfpeck,", 21);
        sayAfter("Jamiroquai,", 22);
        sayAfter("Gorillaz", 23);
        sayAfter("Queens of the Stone Age,", 24);
        sayAfter("and many more artists.", 25);
        sayAfter("Don't worry, it's not personal.", 29);
        sayAfter("I have made this decision purely out of hate.", 32);
        sayAfter("And as your president, I hate you all equally as much.", 35);
        sayAfter("I hope you all understand. Any questions?", 39);
        scheduleAfter(() -> journalistResponds(mm("Does Taylor Swift also fall under this ban?")), TaskSchedule.seconds(43));
        sayAfter("Depends... do you think Taylor Swift makes good music?", 48);
        scheduleAfter(this::presidentTakesSip, TaskSchedule.seconds(50));
        sayAfter("Now, I hope I have informed you all enou-gg-gh-h-h.", 55);
        sayAfter("I can-t-t-t....", 57);
        scheduleAfter(this::presidentPoisoned, TaskSchedule.seconds(58));
        sayAfter("Curse you all!", 67);
        scheduleAfter(this::peopleCelebrate, TaskSchedule.seconds(74));
        return TaskSchedule.seconds(80);
    }

    private void presidentPoisoned() {
        scheduleRepeat(() -> {
            scene.getPlayer().sendPacket(
                new DamageEventPacket(scene.getPresident().getEntityId(),
                        new Damage(DamageType.INDIRECT_MAGIC, null,null, null, 0).getTypeId(),
                        0, 0, null)
            );
            scene.getPlayer().playSound(Sound.sound(Key.key("entity.pillager.hurt"), Sound.Source.MASTER, 1, 1));
        }, TaskSchedule.immediate(), TaskSchedule.tick(20), 6);

        scheduleAfter(() -> {
            scene.getPresident().kill();
            scene.getPlayer().playSound(Sound.sound(Key.key("entity.pillager.death"), Sound.Source.MASTER, 1, 1));
        }, TaskSchedule.seconds(10));
        scheduleAfter(() -> scene.getPresident().remove(), TaskSchedule.seconds(12));
    }
}
