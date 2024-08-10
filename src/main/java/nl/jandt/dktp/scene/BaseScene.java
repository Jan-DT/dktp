package nl.jandt.dktp.scene;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Instance;
import nl.jandt.dktp.CustomPlayer;

import java.util.List;
import java.util.function.Consumer;


public abstract class BaseScene implements Scene {
    private final String sceneId;
    private final EventNode<Event> eventNode;
    private final CustomPlayer player;
    private final Instance instance;

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


}
