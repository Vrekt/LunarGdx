package gdx.lunar.server.network.connection;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.world.World;
import io.netty.channel.Channel;

/**
 * Represents the default player connection handler.
 */
public class ServerPlayerConnection extends ServerAbstractConnection implements ClientPacketHandler {

    /**
     * The player who owns this connection.
     */
    protected LunarServerPlayerEntity player;
    protected boolean disconnected;

    public ServerPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    public void setPlayer(LunarServerPlayerEntity player) {
        this.player = player;
    }

    /**
     * @return {@code  true} if this {connection} is disconnected
     */
    public boolean isDisconnected() {
        return disconnected;
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

        final World world = server.getWorldManager().getWorld(packet.getWorldName());
        if (world.isFull()) {
            return;
        }

        this.player = new LunarServerPlayerEntity(true, server, this);
        this.player.setEntityName(packet.getUsername());
        this.player.setServerWorldIn(world);
        this.player.setEntityId(world.assignEntityIdFor(true));
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
        final int entityId = packet.getEntityId();
        if (player != null && player.getWorld().hasPlayer(entityId)) {
            player.getWorld()
                    .getPlayer(entityId)
                    .getVelocityComponent()
                    .setForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY());
            player.getWorld().broadcastWithExclusion(entityId, new SPacketApplyEntityBodyForce(entityId, packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY()));
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
    public void handleEnterInstance(CPacketEnterInstance packet) {
        System.err.println("requesting enter instance: " + packet.getInstanceId());
      /*  if (this.player != null) {
            final Instance instance = player.getWorld().getInstance(packet.getInstanceId());
            if (instance != null) {
                if (instance.isFull()) {
                    sendImmediately(new SPacketEnterInstance(packet.getInstanceId(), false, true, "Instance is full"));
                } else {
                    this.player.setInInstance(true);
                    this.player.setInstanceIn(instance);
                    sendImmediately(new SPacketEnterInstance(packet.getInstanceId(), true, false, ""));
                    System.err.println("player has joined instance " + packet.getInstanceId());
                }
            } else {
                sendImmediately(new SPacketEnterInstance(packet.getInstanceId(), false, false, "Invalid instance ID"));
            }
        } else {
            sendImmediately(new SPacketEnterInstance(packet.getInstanceId(), false, false, "Invalid player"));
        }*/
    }

    @Override
    public void handlePing(CPacketPing packet) {
        sendImmediately(new SPacketPing(packet.getTime(), System.currentTimeMillis()));
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

    @Override
    public void connectionClosed(Throwable exception) {
        disconnect();
    }
}
