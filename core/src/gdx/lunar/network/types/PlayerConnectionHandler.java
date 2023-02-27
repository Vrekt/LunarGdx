package gdx.lunar.network.types;

import com.badlogic.gdx.Gdx;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;
import lunar.shared.player.impl.LunarPlayerMP;

import java.util.HashMap;
import java.util.Map;

/**
 * Will extend {@link gdx.lunar.network.PlayerConnection} and do some things automatically.
 */
public class PlayerConnectionHandler extends PlayerConnection {

    private final Map<ConnectionOption, Boolean> options = new HashMap<>();
    private float width, height, scaling;

    public PlayerConnectionHandler(Channel channel, LunarProtocol protocol) {
        super(channel, protocol);
    }

    public void setDefaultPlayerSize(float width, float height, float scaling) {
        this.width = width;
        this.height = height;
        this.scaling = scaling;
    }

    public void setDefaultPlayerSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.scaling = 1.0f;
    }

    public void enableOptions(ConnectionOption... options) {
        for (ConnectionOption option : options) {
            this.options.put(option, true);
        }
    }

    public void disableOptions(ConnectionOption... options) {
        for (ConnectionOption option : options) {
            this.options.put(option, true);
        }
    }

    public boolean isOptionEnabled(ConnectionOption option) {
        return options.getOrDefault(option, false);
    }

    @Override
    protected boolean handle(ConnectionOption handler, Packet packet) {
        return super.handle(handler, packet);
    }

    protected boolean isLocalPlayer(int id) {
        return id == local.getEntityId();
    }

    protected boolean shouldHandle(int id, Packet packet, ConnectionOption option) {
        return handle(option, packet) || !isLocalPlayer(id) && !isOptionEnabled(option);
    }

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

        final LunarPlayerMP player = new LunarPlayerMP(true);
        // load player assets.
        player.load();

        // might be removed later.
        player.setIgnorePlayerCollision(true);
        player.getProperties().setProperties(packet.getEntityId(), packet.getUsername());
        // set your local game properties
        player.setSize(width, height, scaling);
        // spawn player in your local world.
        player.spawnEntityInWorld(getWorldIn(), packet.getX(), packet.getY());
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_LEAVE)) return;

        if (verifyPlayerExists(packet.getEntityId()))
            getWorldIn().removeEntityInWorld(getWorldIn().getNetworkPlayer(packet.getEntityId()));
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_POSITION)) return;
        if (!verifyPlayerExists(packet.getEntityId())) return;

        getWorldIn().getNetworkPlayer(packet.getEntityId()).updatePosition(packet.getX(), packet.getY(), packet.getRotation());
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_VELOCITY)) return;
        if (!verifyPlayerExists(packet.getEntityId())) return;

        getWorldIn().getNetworkPlayer(packet.getEntityId()).updateVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
    }

    @Override
    public void handleEntityBodyForce(SPacketApplyEntityBodyForce packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_PLAYER_FORCE)) return;
        if (!verifyPlayerExists(packet.getEntityId())) return;

        getWorldIn().getNetworkPlayer(packet.getEntityId())
                .updateBodyForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY());
    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {
        if (shouldHandle(packet.getEntityId(), packet, ConnectionOption.HANDLE_SET_ENTITY_PROPERTIES)) return;
        if (!verifyPlayerExists(packet.getEntityId())) return;

        getWorldIn().getNetworkPlayer(packet.getEntityId()).setProperties(packet.getEntityName(), packet.getEntityId());
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
        //if (shouldHandle(packet, ConnectionOption.HANDLE_JOIN_INSTANCE)) return;
        if (packet.isAllowed()) {
            getWorldIn().getInstanceFromId(packet.getInstanceId()).enterInstance(true, local.getWorldIn());
            Gdx.app.log("PlayerConnectionHandler", "Instance " + packet.getInstanceId() + " joined.");
        } else if (packet.isFull()) {
            Gdx.app.log("PlayerConnectionHandler", "Instance " + packet.getInstanceId() + " is full");
        } else {
            Gdx.app.log("PlayerConnectionHandler", "Instance " + packet.getInstanceId() + " not-allowed: " + packet.getNotAllowedReason());
        }
    }

    @Override
    public void handlePlayerEnterInstance(SPacketPlayerEnterInstance packet) {
        // TODO
    }
}
