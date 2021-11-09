package gdx.lunar.network.v2;

import gdx.lunar.entity.playerv2.LunarEntityPlayer;
import gdx.lunar.entity.playerv2.impl.LunarPlayerMP;
import gdx.lunar.entity.playerv2.mp.LunarNetworkEntityPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.client.CPacketAuthentication;
import gdx.lunar.protocol.packet.permission.PermissionAttachment;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;

/**
 * Default implementation of {@link gdx.lunar.network.AbstractConnection}
 */
public class PlayerConnection extends AbstractConnection {

    // local player this connection is related to.
    protected LunarEntityPlayer local;

    public PlayerConnection(Channel channel, LunarProtocol protocol) {
        super(channel, protocol);
    }

    public void setLocalPlayer(LunarEntityPlayer local) {
        this.local = local;
    }

    @Override
    public void handlePermissionAttachment(PermissionAttachment permission) {
        switch (permission.getPacketIdFrom()) {
            case CPacketAuthentication.PID:
                if (!permission.hasPermission()) {
                    handleAuthenticationFailed(permission.getPermissionReason());
                    this.close();
                }
                break;
        }
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packet) {
        handleDisconnection();
        this.close();
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        // checks if the handle method was handled, if not automatically spawn them.
        if (!handleCreatePlayer(packet.getUsername(), packet.getEntityId(), packet.getX(), packet.getY())) {
            final LunarNetworkEntityPlayer player = new LunarPlayerMP(true);
            player.getProperties().initialize(packet.getEntityId(), packet.getUsername());
            player.getConfig().setConfig(16, 16, (1 / 16.0f));
            this.local.getInstance().worldIn.spawnEntityInWorld(player);
        }
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (!handleRemovePlayer(packet.getEntityId())) {
            this.local.getInstance().worldIn.removeEntityInWorld(packet.getEntityId(), true);
        }
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

    @Override
    public void handleAuthenticationFailed(String reason) {

    }

    @Override
    public void handleEntityJoinedWorld() {

    }

    @Override
    public void handleDisconnection() {
        if (local.isInWorld()) local.removeEntityInWorld(local.getInstance().worldIn);
    }

    @Override
    public boolean handleCreatePlayer(String username, int entityId, float x, float y) {
        return false;
    }

    @Override
    public boolean handleRemovePlayer(int entityId) {
        return false;
    }
}
