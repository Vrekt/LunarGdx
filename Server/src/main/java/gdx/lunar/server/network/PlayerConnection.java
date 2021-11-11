package gdx.lunar.server.network;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.SPacketApplyEntityBodyForce;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.entity.player.LunarPlayer;
import io.netty.channel.Channel;

/**
 * Represents the default player connection handler.
 */
public class PlayerConnection extends AbstractConnection implements ClientPacketHandler {

    /**
     * The player who owns this connection.
     */
    private LunarPlayer player;
    private boolean disconnected;

    public PlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    public void setPlayer(LunarPlayer player) {
        this.player = player;
    }

    @Override
    public void handleAuthentication(CPacketAuthentication packet) {
        if (server.handlePlayerAuthentication(packet.getGameVersion(), packet.getProtocolVersion())) {
            if (!server.handleJoinProcess(this)) {
                channel.close();
            }
        } else {
            channel.close();
        }
    }

    @Override
    public void handleDisconnect(CPacketDisconnect packet) {
        if (player != null) {
            if (player.getWorld() != null) player.getWorld().removePlayerInWorld(player);
            server.handlePlayerDisconnect(player);
        }
        disconnect();
    }

    @Override
    public void handlePlayerPosition(CPacketPosition packet) {
        if (player != null && player.inWorld()) {
            player.setPosition(packet.getX(), packet.getY());
            player.setRotation(packet.getRotation());
        }
    }

    @Override
    public void handlePlayerVelocity(CPacketVelocity packet) {
        if (player != null && player.inWorld()) {
            player.setVelocity(packet.getVelocityX(), packet.getVelocityY());
            player.setRotation(packet.getRotation());
        }
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        // TODO: this.player = new LunarPlayer();
    }

    @Override
    public void handleWorldLoaded(CPacketWorldLoaded packet) {
        if (player != null) {
            player.setLoaded(true);
            player.getWorld().spawnPlayerInWorld(player);
        }
    }

    @Override
    public void handleBodyForce(CPacketApplyEntityBodyForce packet) {
        if (player != null && player.getWorld() != null) {
            player.getWorld().broadcastPacketInWorld(new SPacketApplyEntityBodyForce(alloc(), packet));
        }
    }

    @Override
    public void handleRequestSpawnEntity(CPacketRequestSpawnEntity packet) {

    }

    @Override
    public void handleSetProperties(CPacketSetProperties packet) {

    }

    @Override
    public void handleCreateLobby(CPacketCreateLobby packet) {

    }

    @Override
    public void handleJoinLobby(CPacketJoinLobby packet) {

    }

    @Override
    public void handleNetworkTile(CPacketNetworkedTile packet) {

    }

    @Override
    public void connectionClosed() {
        disconnect();
    }

    @Override
    public void disconnect() {
        if (disconnected) return;
        this.disconnected = true;

        if (server != null) server.removePlayerConnection(this);
        if (player.inWorld()) player.getWorld().removePlayerInWorld(player);

        channel.pipeline().remove(this);
        channel.close();
    }
}
