package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentBecomeEntityAnimation extends PresidentAnimation {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public PresidentBecomeEntityAnimation(PresidentScene scene) {
        super(scene);
    }

    @Override
    public TaskSchedule trigger() {
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        sayAfter("Well good afternoon.", 1);
        sayAfter("As your president I like to bring you bad news,", 5);
        sayAfter("and boy do I have some.", 9);
        sayAfter("The Minestan Federal Reserve is empty.", 13);
        sayAfter("I might or might not have gambled it all away.", 16);
        sayAfter("You know what they say, you win some you lose some.", 20);
        sayAfter("And in this case, I lost 14 trillion emeralds.", 24);
        sayAfter("Let's drink to that then, huh?", 28);
        scheduleAfter(this::presidentTakesSip, TaskSchedule.seconds(30));
        scheduleAfter(this::presidentBecomeEntity, TaskSchedule.seconds(34));
        scheduleAfter(() -> presidentMoos(mm("moooooo")), TaskSchedule.seconds(35));
        scheduleAfter(() -> presidentMoos(mm("mooo?")), TaskSchedule.seconds(38));
        return TaskSchedule.seconds(42);
    }

    protected void presidentBecomeEntity() {
        scene.getPresident().setInvisible(true);
        final var e = new Entity(EntityType.COW);
        e.setInstance(scene.getInstance(), scene.getPresident().getPosition());
        scene.getPlayer().sendPackets(
                new ParticlePacket(Particle.GUST, scene.getPresident().getPosition().withY(y -> y + .5), Vec.ONE.mul(0.2), 0, 2),
                new ParticlePacket(Particle.POOF, scene.getPresident().getPosition(), Vec.ONE.mul(0.6), 0.5f, 10)
        );

        scheduleAfter(() -> {
            e.remove();
            scene.getPresident().setInvisible(false);
        }, TaskSchedule.seconds(15));
    }

    protected void presidentMoos(Component message) {
        scene.getPlayer().playSound(Sound.sound(Key.key("entity.cow.ambient"), Sound.Source.MASTER, 1, 1), scene.getPresident().getPosition());
        scene.getPlayer().sendMessage(mm.deserialize("<#4444dd>Pres. Cow: <#ffffff><message>",
                Placeholder.component("message", message)));
    }

//    protected EntityType randomEntityType() {
//        final var entityTypes = EntityType.values().stream().toList();
//        final var random = new Random(scene.getPoison().getIngredientSum());
//        return entityTypes.get(random.nextInt(0, entityTypes.size()-1));
//    }
}
