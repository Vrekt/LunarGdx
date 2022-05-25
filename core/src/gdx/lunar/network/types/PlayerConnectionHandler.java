package gdx.lunar.network.types;

import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;
import lunar.shared.entity.player.impl.LunarPlayerMP;

import java.util.HashMap;
import java.util.Map;

/**
 * Will extend {@link gdx.lunar.network.PlayerConnection} and do some things automatically.
 */
public class PlayerConnectionHandler extends PlayerConnection {

    private final Map<ConnectionOption, Boolean> options = new HashMap<>();

    public PlayerConnectionHandler(Channel channel, LunarProtocol protocol) {
        super(channel, protocol);
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
        return super.handle(handler, packet) && options.getOrDefault(handler, false);
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
        handle(ConnectionOption.DISCONNECT, packet);
        this.close();
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        if (handle(ConnectionOption.HANDLE_PLAYER_JOIN, packet)) return;

        final LunarPlayerMP player = new LunarPlayerMP(true);
        // load player assets.
        player.load();

        player.setIgnorePlayerCollision(true);
        player.getProperties().initialize(packet.getEntityId(), packet.getUsername());
        // set your local game properties
        player.setConfig(16, 16, (1 / 16.0f));
        // spawn player in your local world.
        player.spawnEntityInWorld(getWorldIn(), packet.getX(), packet.getY());
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (handle(ConnectionOption.HANDLE_PLAYER_LEAVE, packet)) return;
        if (verifyPlayerExists(packet.getEntityId())) {
            getWorldIn().removeEntityInWorld(getWorldIn().getNetworkPlayer(packet.getEntityId()));
        }
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        if (!verifyPlayerExists(packet.getEntityId()) || handle(ConnectionOption.HANDLE_PLAYER_POSITION, packet))
            return;

        getWorldIn().getNetworkPlayer(packet.getEntityId()).updateServerPosition(packet.getX(), packet.getY(), packet.getRotation());
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        if (!verifyPlayerExists(packet.getEntityId()) || handle(ConnectionOption.HANDLE_PLAYER_VELOCITY, packet))
            return;

        getWorldIn().getNetworkPlayer(packet.getEntityId()).updateServerVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
    }

    @Override
    public void handleEntityBodyForce(SPacketApplyEntityBodyForce packet) {
        if (!verifyPlayerExists(packet.getEntityId()) || handle(ConnectionOption.HANDLE_PLAYER_FORCE, packet))
            return;

        getWorldIn().getNetworkPlayer(packet.getEntityId())
                .updateServerForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY());
    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {
        if (!verifyPlayerExists(packet.getEntityId()) || handle(ConnectionOption.SET_ENTITY_PROPERTIES, packet))
            return;

        getWorldIn().getNetworkPlayer(packet.getEntityId()).setProperties(packet.getEntityName(), packet.getEntityId());
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
        // TODO
    }

    @Override
    public void handlePlayerEnterInstance(SPacketPlayerEnterInstance packet) {
        // TODO
    }
}
