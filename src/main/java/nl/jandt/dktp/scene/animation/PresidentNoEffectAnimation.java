package nl.jandt.dktp.scene.animation;

import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.jandt.dktp.scene.BaseScene;
import nl.jandt.dktp.scene.PresidentScene;

public class PresidentNoEffectAnimation implements Animation {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void trigger(BaseScene scene) {
        final var s = (PresidentScene) scene;
        s.presidentSays(mm.deserialize());
    }
}
