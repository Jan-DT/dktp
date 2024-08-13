package nl.jandt.dktp.scene.animation;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentFloatAnimation extends PresidentAnimation {

    public PresidentFloatAnimation(PresidentScene scene) {
        super(scene);
    }

    @Override
    public TaskSchedule trigger() {
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        sayAfter("Good afternoon nitwits and nobodies.", 1);
        sayAfter("I think you all know why I gathered you.", 5);
        sayAfter("We are hard at work cracking down on poor people.", 9);
        sayAfter("Those who do not have enough emeralds to buy food,", 13);
        sayAfter("are all to be forcefully evicted.", 16);
        sayAfter("This way, we keep housing available for those who can afford it.", 19);
        sayAfter("Now, this has lead to some protests throughout Minestan.", 24);
        sayAfter("And I can assure you,", 28);
        sayAfter("you can feel safe knowing we <i>will</i> violently shut these down.", 31);
        sayAfter("I want you to know that your opinion does not matter to me.", 36);
        scheduleAfter(this::presidentTakesSip, TaskSchedule.seconds(40));
        sayAfter("I hope I have made this very clear.", 44);
        scheduleAfter(this::presidentFloats, TaskSchedule.seconds(46));
        sayAfter("Thank you for your......", 47);
        sayAfter("whaaaaaa! Get me down!", 50);
        return TaskSchedule.seconds(55);
    }

    protected void presidentFloats() {
        scene.getPresident().setNoGravity(true);
        scene.getPresident().setVelocity(new Vec(0, 0.4, 0));
        scheduleRepeat(() -> scene.getPlayer().sendPackets(
                        new ParticlePacket(Particle.CLOUD, scene.getPresident().getPosition(), new Vec(0.5, 0, 0.5), 0.05f, 3)
                ), TaskSchedule.immediate(), TaskSchedule.tick(4), 5 * 9);
        scheduleRepeat(() -> scene.getPresident().teleport(scene.getPresident().getPosition()
                .withPitch(scene.getPresident().getPosition().pitch()+3f)),
                TaskSchedule.millis(1500), TaskSchedule.tick(1), 20);
        scheduleRepeat(() -> scene.getPlayer()
                .sendPacket(new EntityAnimationPacket(scene.getPresident().getEntityId(), EntityAnimationPacket.Animation.SWING_MAIN_ARM)),
                TaskSchedule.seconds(4), TaskSchedule.tick(5), 10);

        scheduleAfter(() -> {
            scene.getPresident().setVelocity(Vec.ZERO);
            scene.getPresident().teleport(PresidentScene.PODIUM_POS);
        }, TaskSchedule.seconds(10));
    }
}
