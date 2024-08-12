package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.text.Component;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.BaseScene;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentNoEffectAnimation extends PresidentAnimation {

    @Override
    public TaskSchedule trigger(BaseScene scene) {
        this.scene = (PresidentScene) scene;
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        sayAfter("Hello everybody.", 1);
        sayAfter("I have called this press conference after some allegations", 5);
        sayAfter("People are claiming that I am a puppet for the Rav & Ger coorporation", 10);
        sayAfter("They say that I am corrupt,", 15);
        sayAfter("and receiving money from the pillagers.", 18);
        sayAfter("And all I can say is they are absolutely cor-", 23);
        sayAfter("wrong. Who would make such unfounded claims.", 26);
        scheduleAfter(this::presidentTakesSip, TaskSchedule.seconds(29));
        sayAfter("I needed that sip.", 34);
        sayAfter("Now, the people of this country have it wrong:", 38);
        sayAfter("Pillagers are NOT the problem,", 42);
        sayAfter("and you wont convince me otherwise.", 45);
        sayAfter("Your villages might have been burnt down by them,", 49);
        sayAfter("and your emeralds might have been stolen,", 53);
        sayAfter("but they are nice people.", 57);
        sayAfter("Believe me,", 60);
        sayAfter("I have spoken to them.", 63);
        sayAfter("Also,", 68);
        sayAfter("the Rav & Ger stores have 35% off all crossbows.", 70);
        sayAfter("It's a nice deal, I would say myself.", 75);
        sayAfter("Thank you all for your attention.", 80);
        return TaskSchedule.seconds(85);
    }
}
