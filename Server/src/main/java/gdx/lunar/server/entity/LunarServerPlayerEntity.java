package gdx.lunar.server.entity;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketDisconnect;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import gdx.lunar.server.world.World;

/**
 * Represents a player entity within the server.
 */
public class LunarServerPlayerEntity extends LunarServerEntity {

    protected LunarServer server;
    protected ServerAbstractConnection connection;
    protected World serverWorldIn;

    // if this player has been loaded client-side into the world.
    protected boolean isLoaded;

    public LunarServerPlayerEntity(Entity entity, boolean initializeComponents, LunarServer server, ServerAbstractConnection connection) {
        super(entity, initializeComponents);
        this.server = server;
        this.connection = connection;
    }

    public LunarServerPlayerEntity(boolean initializeComponents, LunarServer server, ServerAbstractConnection connection) {
        super(initializeComponents);
        this.server = server;
        this.connection = connection;
    }

    public void setServer(LunarServer server) {
        this.server = server;
    }

    /**
     * @return server
     */
    public LunarServer getServer() {
        return server;
    }

    /**
     * @return the players connection
     */
    public ServerAbstractConnection getServerConnection() {
        return connection;
    }

    /**
     * @return the world the player is in.
     */
    public World getWorld() {
        return serverWorldIn;
    }

    public void setServerWorldIn(World serverWorldIn) {
        this.serverWorldIn = serverWorldIn;
    }

    /**
     * @return {@code true} if this player is in a world.
     */
    public boolean inWorld() {
        return serverWorldIn != null;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void kickPlayer(String reason) {
        connection.sendImmediately(new SPacketDisconnect(reason));
        connection.disconnect();
    }

    /**
     * Send this player's information to the other player
     *
     * @param other the other player
     */
    public void sendPlayerToPlayer(LunarServerPlayerEntity other) {
        other.getServerConnection().sendImmediately(new SPacketCreatePlayer(getName(), getEntityId(), getPosition().x, getPosition().y));
    }

    @Override
    public void dispose() {
        super.dispose();
        isLoaded = false;
    }
}
