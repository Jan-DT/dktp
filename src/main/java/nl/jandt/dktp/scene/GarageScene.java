package nl.jandt.dktp.scene;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import nl.jandt.dktp.CustomPlayer;
import nl.jandt.dktp.poison.Poison;
import nl.jandt.dktp.poison.PoisonIngredient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class GarageScene extends BaseScene {
    private static final Pos spawnPos = new Pos(26.5, 1, 0.5, 90, 0);
    private static final Logger log = LoggerFactory.getLogger(GarageScene.class);
    private final Scheduler scheduler = MinecraftServer.getSchedulerManager();
    private final int seed;

    private final List<Entity> entities = new ArrayList<>();
    private Poison poison;
    private ItemDisplayEntity poisonDisplay;

    public GarageScene(CustomPlayer player, int seed) {
        super("garage-scene", player);

        this.seed = seed;
        this.poison = new Poison(seed);

        eventNode().addListener(PlayerBlockInteractEvent.class, this::blockInteractEvent);
    }

    private void blockInteractEvent(@NotNull PlayerBlockInteractEvent event) {
        log.debug("blockInteract");
        if (event.getPlayer() != getPlayer()) return;

        if (event.getBlockPosition().sameBlock(new Vec(24, 2, 0))) {
            if (!getPlayer().getItemInMainHand().isAir()) {

                poison.addIngredient(new PoisonIngredient(null, null));
                poisonDisplay.updateItem(poison.getItem());
            }
        }
    }

    @Override
    public void start() {
        getPlayer().addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 20, 0));
        getPlayer().addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 20, 0));
        getPlayer().addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 127, 20, 0));

        getPlayer().setHeldItemSlot((byte) 4);
        getPlayer().setItemInMainHand(ItemStack.of(Material.STONE));

        poisonDisplay = new ItemDisplayEntity(EntityType.ITEM_DISPLAY);
        final var standMeta = (ItemDisplayMeta) poisonDisplay.getEntityMeta();

        standMeta.setDisplayContext(ItemDisplayMeta.DisplayContext.FIXED);
        standMeta.setHasNoGravity(true);
        standMeta.setScale(Vec.ONE.mul(0.8));
        entities.add(poisonDisplay);
        poisonDisplay.updateItem(poison.getItem());

        poisonDisplay.setInstance(getInstance(), new Pos(24.82, 2.60, 0.5, 270, 0));

        getPlayer().setRespawnPoint(spawnPos);
        getPlayer().teleport(spawnPos);
        scheduler.scheduleTask(() -> {
            getPlayer().sendActionBar(Component.text("welcome in garage"));
        }, TaskSchedule.seconds(5), TaskSchedule.stop());
    }

    @Override
    public void end() {
    }
}
