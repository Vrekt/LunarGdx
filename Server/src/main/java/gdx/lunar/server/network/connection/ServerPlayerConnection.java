package gdx.lunar.server.network.connection;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.SPacketApplyEntityBodyForce;
import gdx.lunar.protocol.packet.server.SPacketAuthentication;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketWorldInvalid;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.world.ServerWorld;
import io.netty.channel.Channel;

/**
 * Represents the default player connection handler.
 */
public class ServerPlayerConnection extends ServerAbstractConnection implements ClientPacketHandler {

    /**
     * The player who owns this connection.
     */
    private LunarServerPlayerEntity player;
    private boolean disconnected;

    public ServerPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    public void setPlayer(LunarServerPlayerEntity player) {
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
            player.getWorld().handlePlayerPosition(player, packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    @Override
    public void handlePlayerVelocity(CPacketVelocity packet) {
        if (player != null && player.inWorld()) {
            player.getWorld().handlePlayerVelocity(player, packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
        }
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        if (packet.getUsername() == null) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "Invalid username."));
            return;
        } else if (!server.getWorldManager().worldExists(packet.getWorldName())) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "World does not exist."));
            return;
        }

        final ServerWorld world = server.getWorldManager().getWorld(packet.getWorldName());
        if (world.isWorldFull()) {
            return;
        }

        this.player = new LunarServerPlayerEntity(true, server, this);
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
        if (player != null && player.getWorld().hasNetworkPlayer(packet.getEntityId())) {
            player.getWorld().getNetworkPlayer(packet.getEntityId()).getVelocityComponent().setForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY());
            player.getWorld().broadcast(packet.getEntityId(), new SPacketApplyEntityBodyForce(packet.getEntityId(), packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY()));
        }
    }

    @Override
    public void handleRequestSpawnEntity(CPacketRequestSpawnEntity packet) {

    }

    @Override
    public void handleSetProperties(CPacketSetProperties packet) {
        player.setEntityName(packet.getUsername());
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
