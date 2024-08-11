package nl.jandt.dktp;

import net.hollowcube.polar.*;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;
import nl.jandt.dktp.scene.GarageScene;
import nl.jandt.dktp.scene.PresidentScene;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    private static final Map<String, PolarWorld> worlds = new HashMap<>();
    private static final SceneManager sceneManager = new SceneManager();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private static final UUID packUUID = UUID.fromString("8ab8fad0-c49b-41cd-97c3-1d501a3825e0");
    private static final URI packUri = URI.create("https://connect.jandt.nl/static/dktp-resource-pack-v1.zip");
    private static final String packHash = "0c0f25936fc344d9018567876de965179c333de0";
    private static final ResourcePackRequest resourcePack = ResourcePackRequest.resourcePackRequest()
            .packs(ResourcePackInfo.resourcePackInfo(packUUID, packUri, packHash))
            .prompt(mm.deserialize("<#33ff33>This resource pack is completely optional, but it does improve your experience."))
            .required(false).build();

    private static final Random random = new Random();

    public static void main(String[] args) {
        log.info("Initializing server...");
        final var server = MinecraftServer.init();

        log.info("Loading worlds...");
        loadWorlds(worlds, "worlds");
        log.info("Loaded {} worlds successfully.", worlds.size());

        final var eventHandler = MinecraftServer.getGlobalEventHandler();


        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, e -> {
            log.info("Configuring player '{}'...", e.getPlayer());
            e.setSpawningInstance(makeInstance(worlds.get("dktp1")));
            e.getPlayer().setRespawnPoint(new Pos(0, 0, 0));

            e.getPlayer().sendResourcePacks(resourcePack);
        });

        eventHandler.addListener(PlayerSpawnEvent.class, e -> {
            e.getPlayer().setHeldItemSlot((byte) 4);

            if (!e.isFirstSpawn()) return;

            startGame((CustomPlayer) e.getPlayer());
        });

        final var commandManager = MinecraftServer.getCommandManager();
        final var resetCommand = new Command("reset");
        resetCommand.addSyntax((sender, context) -> {
            ((CustomPlayer) sender).setInstance(makeInstance(worlds.get("dktp1")));
            startGame((CustomPlayer) sender);
        });
        commandManager.register(resetCommand);

        eventHandler.addListener(PlayerSwapItemEvent.class, e -> e.setCancelled(true));
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, e -> e.setCancelled(true));
        eventHandler.addListener(PlayerBlockBreakEvent.class, e -> e.setCancelled(true));
        eventHandler.addListener(PlayerBlockPlaceEvent.class, e -> e.setCancelled(true));

        MinecraftServer.getConnectionManager().setPlayerProvider(CustomPlayer::new);

        server.start("0.0.0.0", 25565);
    }

    static @NotNull Instance makeInstance(PolarWorld world) {
        final var instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkLoader(new PolarLoader(world));
        instance.setChunkSupplier(LightingChunk::new);

        return instance;
    }

    static void startGame(CustomPlayer player) {
        getSceneManager().switchScene(player, new GarageScene(player, random.nextInt()));
//            getSceneManager().switchScene(player, new PresidentScene(player));
        player.setGameMode(GameMode.ADVENTURE);
    }

    static void loadWorlds(Map<String, PolarWorld> worldMap, String worldDir) {
        final var worldsPath = Path.of("", worldDir);
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
                    log.error("Failed to import world '{}': {}", path, e.toString());
                    continue;
                }
                log.debug("Imported world '{}'", path.toString());
            }
        } catch (NoSuchFileException e) {
            log.debug("Import directory at '{}' not located, skipping...", importPath);
        } catch (IOException e) {
            log.error("Failed to read world import directory: {}", e.toString());
        }

        // load polar worlds
        final var loadedWorlds = new HashMap<String, PolarWorld>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(worldsPath)) {
            for (Path path : stream) {
                // checks if path should be valid polar file
                if (Files.isDirectory(path) || !path.toString().endsWith(".polar")) continue;

                try (var reader = new FileInputStream(path.toFile())) {
                    final PolarWorld world = PolarReader.read(reader.readAllBytes());
                    final var worldName = path.getFileName().toString().replace(".polar", "");
                    loadedWorlds.put(worldName, world);
                } catch (Exception e) {
                    log.error("Failed to load world '{}': {}", path, e.toString());
                    continue;
                }
                log.debug("Loaded world '{}'", path.toString());
            }
        } catch (IOException e) {
            log.error("Failed to read world directory: {}", e.toString());
        }

        worldMap.putAll(loadedWorlds);
    }

    public static Map<String, PolarWorld> worlds() {
        return worlds;
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }
}
