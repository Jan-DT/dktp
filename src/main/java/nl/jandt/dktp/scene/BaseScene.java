package nl.jandt.dktp.scene;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public abstract class BaseScene implements Scene {
    private final String sceneId;
    private final EventNode<Event> eventNode;

    public BaseScene(String id) {
        this.sceneId = id;
        this.eventNode = EventNode.all(id);

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        eventNode.addListener(PlayerEnterSceneEvent.class, e -> {
            if (e.getScene() == this) start();
        });

        eventNode.addListener(PlayerExitSceneEvent.class, e -> {
            if (e.getScene() == this) end();
        });
    }

    protected EventNode<Event> eventNode() {
        return eventNode;
    }

    public String getSceneId() {
        return sceneId;
    }
}
