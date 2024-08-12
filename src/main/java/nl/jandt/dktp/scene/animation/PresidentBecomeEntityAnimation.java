package nl.jandt.dktp.scene.animation;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.BaseScene;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static nl.jandt.dktp.scene.animation.Animation.*;

public class PresidentBecomeEntityAnimation extends PresidentAnimation {

    @Override
    public TaskSchedule trigger(BaseScene scene) {
        this.scene = (PresidentScene) scene;
        return variation1();
    }

    private @NotNull TaskSchedule variation1() {
        scheduleAfter(this::presidentBecomeEntity, TaskSchedule.seconds(1));
        return TaskSchedule.seconds(55);
    }

    protected void presidentBecomeEntity() {
        scene.getPresident().setInvisible(true);
        final var e = new Entity(randomEntityType());
        e.setInstance(scene.getInstance(), scene.getPresident().getPosition());

        scheduleAfter(() -> {
            e.remove();
            scene.getPresident().setInvisible(false);
        }, TaskSchedule.seconds(10));
    }

    protected EntityType randomEntityType() {
        final var entityTypes = EntityType.values().stream().toList();
        final var random = new Random(scene.getPoison().getIngredientSum());
        return entityTypes.get(random.nextInt(0, entityTypes.size()-1));
    }
}
