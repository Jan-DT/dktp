package nl.jandt.dktp.scene;

public interface Scene {
    void start();
    void end();
    boolean isActive();
    int getVisit();
}
