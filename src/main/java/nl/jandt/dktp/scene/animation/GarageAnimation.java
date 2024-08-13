package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.jandt.dktp.scene.GarageScene;
import nl.jandt.dktp.scene.PresidentScene;

public abstract class GarageAnimation implements Animation {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    protected final GarageScene scene;

    public GarageAnimation(GarageScene scene) {
        this.scene = scene;
    }
}
