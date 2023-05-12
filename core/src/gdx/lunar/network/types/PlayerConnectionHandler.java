package gdx.lunar.network.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;
import lunar.shared.entity.player.impl.NetworkPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * A default handler for quick use.
 */
public class PlayerConnectionHandler extends PlayerConnection {

    private final Map<ConnectionOption, Boolean> options = new HashMap<>();

    public PlayerConnectionHandler(Channel channel, LunarProtocol protocol) {
        super(channel, protocol);
    }

    /**
     * Enable option(s) to be handled yourself
     *
     * @param options the options
     */
    public void enableOptions(ConnectionOption... options) {
        for (ConnectionOption option : options) {
            this.options.put(option, true);
        }
    }

    /**
     * Disable option(s) to be handled yourself
     *
     * @param options the options
     */
    public void disableOptions(ConnectionOption... options) {
        for (ConnectionOption option : options) {
            this.options.put(option, true);
        }
    }

    /**
     * Check if an option is enabled
     *
     * @param option the option
     * @return {@code  true} if so
     */
    public boolean isOptionEnabled(ConnectionOption option) {
        return options.getOrDefault(option, false);
    }

    /**
     * Check if the provided id is the local player
     * Used for avoiding packets the client sent and received back
     *
     * @param id the ID
     * @return {@code  true} if so
     */
    protected boolean isLocalPlayer(int id) {
        return id == playerSupplier.getPlayer().getEntityId();
    }

    /**
     * Check if the provided packet should be handled
     *
     * @param id     the entity ID
     * @param packet the packet
     * @param option the option
     * @return {@code  true} if so
     */
    protected boolean shouldHandle(int id, Packet packet, ConnectionOption option) {
        return handle(option, packet) || !isLocalPlayer(id) && !isOptionEnabled(option);
    }

    /**
     * Check if the provided packet should be handled
     *
     * @param packet the packet
     * @param option the option
     * @return {@code  true} if so
     */
    protected boolean shouldHandle(Packet packet, ConnectionOption option) {
        return handle(option, packet) || !isOptionEnabled(option);
    }

    @Override
    public void handleAuthentication(SPacketAuthentication packet) {
        if (!packet.isAllowed()) {
            this.authenticationFailed = true;
            this.close();
        } else {
            this.authenticationFailed = false;
        }
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packet) {
        handle(ConnectionOption.HANDLE_DISCONNECT, packet);
        this.close();
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_JOIN)) return;

        final NetworkPlayer player = new NetworkPlayer(true);
        player.load();
        player.setIgnoreOtherPlayerCollision(true);
        player.getProperties().setProperties(packet.getEntityId(), packet.getUsername());
        player.spawnInWorld(getWorldIn(), new Vector2(packet.getX(), packet.getY()));
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_LEAVE)) return;

        if (doesPlayerExistInWorld(packet.getEntityId()) && getWorldIn() != null) {
            getWorldIn().removePlayerInWorld(packet.getEntityId());
        }
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_POSITION)) return;
        if (!doesPlayerExistInWorld(packet.getEntityId()) || getWorldIn() == null) return;
        getWorldIn().updatePlayerPositionInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_VELOCITY)) return;
        if (!doesPlayerExistInWorld(packet.getEntityId()) || getWorldIn() == null) return;
        getWorldIn().updatePlayerVelocityInWorld(packet.getEntityId(), packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
    }

    @Override
    public void handleEntityBodyForce(SPacketApplyEntityBodyForce packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_FORCE)) return;
        if (!doesPlayerExistInWorld(packet.getEntityId()) || getWorldIn() == null) return;
        // TODO: Update body force
    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_SET_ENTITY_PROPERTIES)) return;
        if (!doesPlayerExistInWorld(packet.getEntityId()) || getWorldIn() == null) return;
        getWorldIn().updatePlayerProperties(packet.getEntityId(), packet.getEntityName(), packet.getEntityId());
    }

    @Override
    public void handleWorldInvalid(SPacketWorldInvalid packet) {
        if (shouldHandle(packet, ConnectionOption.HANDLE_WORLD_INVALID)) return;
        Gdx.app.log("PlayerConnectionHandler", "World " + packet.getWorldName() + " does not exist! (" + packet.getReason() + ")");
    }

    @Override
    public void handleCreateLobby(SPacketCreateLobby packet) {
        // TODO
    }

    @Override
    public void handleJoinLobbyDenied(SPacketJoinLobbyDenied packet) {
        // TODO
    }

    @Override
    public void handleJoinLobby(SPacketJoinLobby packet) {
        // TODO
    }

    @Override
    public void handleEnterInstance(SPacketEnterInstance packet) {
        // TODO?
    }

    @Override
    public void handlePlayerEnterInstance(SPacketPlayerEnterInstance packet) {
        // TODO
    }
}
