package gdx.lunar.server.network.connection;

import com.badlogic.gdx.Gdx;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.server.entity.ServerPlayerEntity;
import gdx.lunar.server.entity.impl.LunarServerPlayerEntity;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.world.World;
import io.netty.channel.Channel;

/**
 * Represents the default player connection handler.
 */
public class ServerPlayerConnection extends ServerAbstractConnection {

    protected ServerPlayerEntity player;
    protected boolean disconnected, hasJoined;

    public ServerPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    /**
     * @return {@code  true} if this {connection} is disconnected
     */
    public boolean isDisconnected() {
        return disconnected;
    }

    @Override
    public void handleAuthentication(C2SPacketAuthenticate packet) {
        if (server.authenticatePlayer(packet.getGameVersion(), packet.getProtocolVersion())) {
            if (server.addPlayerToServer(this)) {
                sendImmediately(new S2CPacketAuthenticate(true, server.getGameVersion(), server.getProtocolVersion()));
            } else {
                // adding the player to the server failed
                sendImmediately(new S2CPacketDisconnected("Server rejected player"));
                channel.close();
            }
        } else {
            channel.close();
        }
    }

    @Override
    public void handleDisconnected(C2SPacketDisconnected packet) {
        // ensure player has actually been connected first
        if (hasJoined) {
            if (player.isInWorld()) player.getWorld().removePlayerInWorld(player);
            server.handlePlayerDisconnect(player);
        }
        disconnect();
    }

    @Override
    public void handlePing(C2SPacketPing packet) {
        sendImmediately(new S2CPacketPing(packet.getTime(), player.getWorld().getTime()));
    }

    @Override
    public void handleJoinWorld(C2SPacketJoinWorld packet) {
        if (!server.isUsernameValidInWorld(packet.getWorldName(), packet.getUsername())) {
            // TODO: May be desirable to instead check specifically whats wrong
            // TODO: Instead of just making the client guess
            sendImmediately(new S2CPacketWorldInvalid(packet.getWorldName(), "Invalid username or world."));
        }

        final World world = server.getWorldManager().getWorld(packet.getWorldName());
        if (world.isFull()) {
            sendImmediately(new S2CPacketWorldInvalid(packet.getWorldName(), "World is full."));
            return;
        }

        player = new LunarServerPlayerEntity(server, this);
        player.setName(packet.getUsername());
        player.setWorldIn(world);
        player.setInWorld(true);
        player.setEntityId(world.assignEntityIdFor(true));
        sendImmediately(new S2CPacketJoinWorld(world.getName(), player.getEntityId(), world.getTime()));
        hasJoined = true;
    }

    @Override
    public void handleWorldLoaded(C2SPacketWorldLoaded packet) {
        if (hasJoined) {
            player.setIsLoaded(true);
            player.getWorld().spawnPlayerInWorld(player);
        }
    }

    @Override
    public void handlePlayerPosition(C2SPacketPlayerPosition packet) {
        if (hasJoined && player.isInWorld()) {
            player.getWorld().handlePlayerPosition(player, packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    @Override
    public void handlePlayerVelocity(C2SPacketPlayerVelocity packet) {
        if (hasJoined && player.isInWorld()) {
            player.getWorld().handlePlayerVelocity(player, packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
        }
    }

    @Override
    public void disconnect() {
        if (disconnected) return;
        this.disconnected = true;

        if (server != null) server.removePlayerConnection(this);
        if (player != null) {
            if (player.isInWorld()) player.getWorld().removePlayerInWorld(player);
            player.dispose();
        }

        channel.pipeline().remove(this);
        if (channel.isOpen()) channel.close();
    }

    @Override
    public void connectionClosed(Throwable exception) {
        if (exception != null) Gdx.app.log("ServerPlayerConnection", "Connection closed with exception!", exception);
        if (!disconnected) disconnect();
    }
}
