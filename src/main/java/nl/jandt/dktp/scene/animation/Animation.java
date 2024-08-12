package nl.jandt.dktp.scene.animation;

import nl.jandt.dktp.scene.BaseScene;

@FunctionalInterface
public interface Animation {
    void trigger(BaseScene scene);
}
