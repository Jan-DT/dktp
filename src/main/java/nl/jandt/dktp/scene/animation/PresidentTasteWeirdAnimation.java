package nl.jandt.dktp.scene.animation;

import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import static nl.jandt.dktp.scene.animation.Animation.scheduleAfter;

public class PresidentTasteWeirdAnimation extends PresidentAnimation {

    public PresidentTasteWeirdAnimation(PresidentScene scene) {
        super(scene);
    }

    @Override
    public TaskSchedule trigger() {
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        sayAfter("Good evening.", 1);
        sayAfter("I would like to announce to you that I was elected as your president once again.", 5);
        sayAfter("The people have voted overwhelmingly in my favor,", 10);
        sayAfter("I love to hear that 97% voted for re-election.", 14);
        sayAfter("I am starting my term with the following changes:", 18);
        sayAfter("Fletchers can no longer trade sticks,", 23);
        sayAfter("mending books can no longer be discounted,", 27);
        sayAfter("and stopping raids will no longer provide discounts.", 30);
        scheduleAfter(this::presidentTakesSip, TaskSchedule.seconds(34));
        sayAfter("Huh... ehw, my drink tastes horrible.", 38);
        sayAfter("It tastes like rotten flesh!", 41);
        sayAfter("Guards, get rid of my horrible chef!", 45);
        sayAfter("Now, where was I?", 50);
        sayAfter("Ah yes, raid discounts. No longer.", 54);
        sayAfter("I hope I to serve me-.. you well.", 57);
        sayAfter("I will be washing my mouth. See you.", 60);
        return TaskSchedule.seconds(68);
    }
}
