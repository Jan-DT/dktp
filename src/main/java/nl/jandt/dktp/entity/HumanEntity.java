package nl.jandt.dktp.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.crypto.ChatSession;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HumanEntity extends LivingEntity {

    private final String username;
    private final PlayerSkin skin;
    private final Component displayName;
    private final @NotNull PlayerInfoUpdatePacket.Entry entry;

    public HumanEntity(String username, PlayerSkin skin) {
        this(UUID.randomUUID(), username, skin, Component.text(username));
    }

    public HumanEntity(String username, PlayerSkin skin, Component displayName) {
        this(UUID.randomUUID(), username, skin, displayName);
    }

    public HumanEntity(UUID uuid, String username, PlayerSkin skin, Component displayName) {
        super(EntityType.PLAYER, uuid);

        this.username = username;
        this.skin = skin;
        this.displayName = displayName;

        // provide skin data
        List<PlayerInfoUpdatePacket.Property> properties;
        if (skin != null) properties = List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature()));
        else properties = List.of();

        // turn on skin layers
        this.editEntityMeta(PlayerMeta.class, meta -> meta.setDisplayedSkinParts((byte) 127));

        // create tab list entry
        this.entry = new PlayerInfoUpdatePacket.Entry(
                this.getUuid(), username,
                properties, false,
                0, GameMode.SURVIVAL,
                displayName, null
        );
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        // send tab list entry
        player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

        // spawn entity
        super.updateNewViewer(player);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        // despawn entity
        super.updateOldViewer(player);

        // delete tab list entry
        player.sendPacket(new PlayerInfoRemovePacket(this.getUuid()));
    }

    public String getUsername() {
        return this.username;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public PlayerSkin getSkin() {
        return skin;
    }
}
