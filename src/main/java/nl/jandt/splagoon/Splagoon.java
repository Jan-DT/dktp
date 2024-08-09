package nl.jandt.splagoon;

import net.hollowcube.polar.AnvilPolar;
import net.hollowcube.polar.PolarReader;
import net.hollowcube.polar.PolarWorld;
import net.hollowcube.polar.PolarWriter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Splagoon {
    private static final Logger log = LoggerFactory.getLogger(Splagoon.class);
    private static final Map<String, PolarWorld> worlds = new HashMap<>();

    public static void main(String[] args) {
        log.info("Initializing server...");
        final var server = MinecraftServer.init();

        log.info("Loading worlds...");
        loadWorlds(worlds, "worlds");
        log.info("Loaded {} worlds successfully.", worlds.size());

        final var eventHandler = MinecraftServer.getGlobalEventHandler();

        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, e -> {
            log.info("Configuring player {}...", e.getPlayer());
        });

        MinecraftServer.getConnectionManager().setPlayerProvider(CustomPlayer::new);

        server.start("0.0.0.0", 25565);
    }

    static void loadWorlds(Map<String, PolarWorld> worldMap, String worldDir) {
        final var worldsPath = Path.of(worldDir);
        final var importPath = worldsPath.resolve("import");

        // import anvil worlds
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(importPath)) {
            for (Path path : stream) {
                final var name = path.getFileName().toString() + ".polar";
                final var newPath = worldsPath.resolve(name);

                // check if world is a non-imported directory
                if (Files.exists(newPath) || !Files.isDirectory(path)) continue;

                final var newPolarFile = Files.createFile(newPath);
                try (var writer = new FileOutputStream(newPolarFile.toFile(), false)) {
                    writer.write(PolarWriter.write(AnvilPolar.anvilToPolar(path)));
                } catch (Exception e) {
                    log.error("Failed to import world {}: {}", path, e.toString());
                    continue;
                }
                log.debug("Imported world {}", path.toString());
            }
        } catch (IOException e) {
            log.error("Failed to read world import directory: {}", e.toString());
        }

        // load polar worlds
        final var loadedWorlds = new HashMap<String, PolarWorld>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(worldsPath)) {
            for (Path path : stream) {
                // checks if path should be valid polar file
                if (Files.isDirectory(path) || !path.endsWith(".polar")) continue;

                try (var reader = new FileInputStream(path.toFile())) {
                    final PolarWorld world = PolarReader.read(reader.readAllBytes());
                    final var worldName = path.getFileName().toString().replace(".polar", "");
                    loadedWorlds.put(worldName, world);
                } catch (Exception e) {
                    log.error("Failed to load world {}: {}", path, e.toString());
                    continue;
                }
                log.debug("Loaded world {}", path.toString());
            }
        } catch (IOException e) {
            log.error("Failed to read world directory: {}", e.toString());
        }

        worldMap.putAll(loadedWorlds);
    }

    public static Map<String, PolarWorld> worlds() {
        return worlds;
    }

}