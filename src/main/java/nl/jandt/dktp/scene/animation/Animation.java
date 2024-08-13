package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.scene.BaseScene;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
public interface Animation {
    TaskSchedule trigger();

    static @NotNull Component mm(String string, TagResolver... resolvers) {
        return MiniMessage.miniMessage().deserialize(string, resolvers);
    }

    static void scheduleAfter(Runnable task, TaskSchedule delayBefore) {
        MinecraftServer.getSchedulerManager().scheduleTask(task, delayBefore, TaskSchedule.stop());
    }

    static void scheduleRepeat(Runnable task, TaskSchedule delayBefore, TaskSchedule delayBetween, int amount) {
        var count = new AtomicInteger();
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            task.run();
            if (count.incrementAndGet() <= amount) return delayBetween;
            else return TaskSchedule.stop();
        }, delayBefore);
    }
}
