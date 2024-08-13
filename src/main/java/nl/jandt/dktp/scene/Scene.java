package nl.jandt.dktp.scene;

public interface Scene {
    void start(SceneContext context);
    void end();
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isActive();
    int getVisit();
}
