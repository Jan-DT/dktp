package nl.jandt.dktp.scene;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class BaseScene implements Scene {
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();

    private final String sceneId;
    private final EventNode<Event> eventNode;
    private final CustomPlayer player;
    private final Instance instance;

    private boolean active = false;
    private int visit = 0;
    private AtomicBoolean lockInteractions = new AtomicBoolean(false);

    public BaseScene(String id, CustomPlayer player, Instance instance) {
        this.sceneId = id;
        this.eventNode = EventNode.all(id);

        this.player = player;
        this.instance = instance;

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        eventNode.addListener(PlayerEnterSceneEvent.class, e -> {
            if (e.getScene() == this) start();
        });

        eventNode.addListener(PlayerExitSceneEvent.class, e -> {
            if (e.getScene() == this) end();
        });
    }

    public BaseScene(String id, CustomPlayer player) {
        this(id, player, player.getInstance());
    }

    protected EventNode<Event> eventNode() {
        return eventNode;
    }

    public String getSceneId() {
        return sceneId;
    }

    public CustomPlayer getPlayer() {
        return player;
    }

    public Instance getInstance() {
        return instance;
    }

    /**
     * Runs a function with interactions locked. Ensuring they will be unlocked after completion.
     * @param runnable Whatever function you want to run
     */
    public void withLockedInteractions(@NotNull Runnable runnable) {
        this.lockInteractions.set(true);
        runnable.run();
        this.lockInteractions.set(false);
    }

    public void withLockedInteractions(@NotNull Runnable runnable, TaskSchedule delay) {
        this.lockInteractions.set(true);
        runnable.run();

        scheduler.scheduleTask(() -> this.lockInteractions.set(false), delay, TaskSchedule.stop());
    }

    protected void lockInteractions(boolean lock) {
        this.lockInteractions.set(lock);
    }

    public boolean interactionsLocked() {
        return this.lockInteractions.get();
    }

    @Override
    public void start() {
        this.active = true;
        this.visit++;
    }

    @Override
    public void end() {
        this.active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public int getVisit() {
        return visit;
    }
}
