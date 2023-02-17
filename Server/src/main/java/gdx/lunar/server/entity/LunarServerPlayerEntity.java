package gdx.lunar.server.entity;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketDisconnect;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.instance.Instance;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import gdx.lunar.server.world.ServerWorld;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;

/**
 * Represents a player entity within the server.
 * extends network entity {@link  LunarNetworkEntityPlayer}
 */
public class LunarServerPlayerEntity extends LunarNetworkEntityPlayer {

    protected LunarServer server;
    protected ServerAbstractConnection connection;
    protected ServerWorld serverWorldIn;
    protected Instance instanceIn;

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

    @Override
    public AbstractConnection getConnection() {
        throw new UnsupportedOperationException("Must use server connection");
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
    public ServerWorld getWorld() {
        return serverWorldIn;
    }

    /**
     * @return {@code true} if this player is in a world.
     */
    public boolean inWorld() {
        return serverWorldIn != null;
    }

    /**
     * @param worldIn world in
     */
    public void setWorldIn(ServerWorld worldIn) {
        this.serverWorldIn = worldIn;
    }

    public Instance getInstanceIn() {
        return instanceIn;
    }

    public void setInstanceIn(Instance instanceIn) {
        this.instanceIn = instanceIn;
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
        isLoaded = false;
    }
}
