package gdx.lunar.server.network;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.SPacketAuthentication;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.entity.player.LunarPlayer;
import gdx.lunar.server.world.World;
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
            } else {
                sendImmediately(new SPacketAuthentication(true));
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
        if (packet.getUsername() == null || !server.getWorldManager().worldExists(packet.getWorldName())) {
            return;
        }

        final World world = server.getWorldManager().getWorld(packet.getWorldName());
        this.player = new LunarPlayer(true, server, this);
        this.player.setEntityName(packet.getUsername());
        this.player.setWorldIn(world);
        this.player.setEntityId(world.assignEntityId());

        sendImmediately(new SPacketJoinWorld(packet.getWorldName(), player.getEntityId()));
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
        if (player != null) {
            player.getVelocityComponent().setForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY());
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
        if (player != null && player.inWorld()) player.getWorld().removePlayerInWorld(player);

        channel.pipeline().remove(this);
        channel.close();
    }
}
