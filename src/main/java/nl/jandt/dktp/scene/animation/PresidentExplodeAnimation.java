package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentExplodeAnimation extends PresidentAnimation {

    public PresidentExplodeAnimation(PresidentScene scene) {
        super(scene);
    }

    @Override
    public TaskSchedule trigger() {
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        sayAfter("Well hello peoples of Minestan.", 1);
        sayAfter("I have gathered you here today to announce new economic policy.", 5);
        sayAfter("From today onwards,", 10);
        sayAfter("40% of your income will go towards the, so-called, innovation enhancement project.", 13);
        respondAfter("But doesn't that money just go to the pillagers through the Rav & Ger coorporation?", 19);
        sayAfter("I have no clue what you are talking about.", 25);
        sayAfter("Further questions can go to my automated response AI.", 30);
        scheduleAfter(this::presidentTakesSip, TaskSchedule.seconds(34));
        sayAfter("Thank you for your att..t..t....", 38);
        scheduleAfter(this::presidentExplodes, TaskSchedule.seconds(39));
        scheduleAfter(this::peopleCelebrate, TaskSchedule.seconds(41));
        return TaskSchedule.seconds(45);
    }

    public void presidentExplodes() {
        scene.getPlayer().sendPackets(
                new ParticlePacket(Particle.EXPLOSION_EMITTER, scene.getPresident().getPosition(), Pos.ZERO, 0, 2),
                new ParticlePacket(Particle.ENTITY_EFFECT.withColor(255, scene.getPoison().getColor()),
                        scene.getPresident().getPosition(), Vec.ONE, 5, 50)
        );
        scene.getPlayer().playSound(Sound.sound(Key.key("entity.generic.explode"), Sound.Source.MASTER, 2, 1));
        scene.getPlayer().playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.MASTER, 2, 1.5f));
        scene.getPresident().kill();
        scheduleAfter(scene.getPresident()::remove, TaskSchedule.seconds(1));
    }
}
