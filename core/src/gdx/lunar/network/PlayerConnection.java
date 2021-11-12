package gdx.lunar.network;

import gdx.lunar.entity.player.impl.LunarPlayerMP;
import gdx.lunar.entity.player.mp.LunarNetworkEntityPlayer;
import gdx.lunar.network.handlers.ConnectionHandlers;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;

/**
 * Default implementation of {@link gdx.lunar.network.AbstractConnection}
 */
public class PlayerConnection extends AbstractConnection {

    private boolean authenticationFailed = false;

    public PlayerConnection(Channel channel, LunarProtocol protocol) {
        super(channel, protocol);
    }

    public boolean isAuthenticationFailed() {
        return authenticationFailed;
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
        this.close();
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        final LunarNetworkEntityPlayer player = new LunarPlayerMP(true);
        player.getProperties().initialize(packet.getEntityId(), packet.getUsername());
        player.getConfig().setConfig(16, 16, (1 / 16.0f));

        this.local.getInstance().worldIn.spawnEntityInWorld(player);
        if (handlers.containsKey(ConnectionHandlers.CREATE_PLAYER))
            handlers.get(ConnectionHandlers.CREATE_PLAYER).accept(packet);
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        this.local.getInstance().worldIn.removeEntityInWorld(packet.getEntityId(), true);

        if (handlers.containsKey(ConnectionHandlers.REMOVE_PLAYER))
            handlers.get(ConnectionHandlers.REMOVE_PLAYER).accept(packet);
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        if (!local.getInstance().worldIn.hasNetworkPlayer(packet.getEntityId())) return;

        this.local.getInstance().worldIn.getNetworkPlayer(packet.getEntityId()).updateServerPosition(packet.getX(), packet.getY(), packet.getRotation());
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        if (!local.getInstance().worldIn.hasNetworkPlayer(packet.getEntityId())) return;

        this.local.getInstance().worldIn.getNetworkPlayer(packet.getEntityId()).updateServerVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
    }

    @Override
    public void handleJoinWorld(SPacketJoinWorld packet) {
        if (handlers.containsKey(ConnectionHandlers.JOIN_WORLD))
            handlers.get(ConnectionHandlers.JOIN_WORLD).accept(packet);
    }

    @Override
    public void handleEntityBodyForce(SPacketApplyEntityBodyForce packet) {
        if (!local.getInstance().worldIn.hasNetworkPlayer(packet.getEntityId())) return;

        local.getInstance().worldIn.getNetworkPlayer(packet.getEntityId()).updateForces(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY());
    }

    @Override
    public void handleSpawnEntity(SPacketSpawnEntity packet) {

    }

    @Override
    public void handleSpawnEntityDenied(SPacketSpawnEntityDenied packet) {

    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {

    }

    @Override
    public void handleCreateLobby(SPacketCreateLobby packet) {

    }

    @Override
    public void handleJoinLobbyDenied(SPacketJoinLobbyDenied packet) {

    }

    @Override
    public void handleJoinLobby(SPacketJoinLobby packet) {

    }

    @Override
    public void handleEnterInstance(SPacketEnterInstance packet) {

    }

    @Override
    public void handlePlayerEnterInstance(SPacketPlayerEnterInstance packet) {

    }
}
