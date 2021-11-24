package gdx.lunar.server.game.entity.player;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketDisconnect;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.entity.LunarEntity;
import gdx.lunar.server.network.AbstractConnection;
import gdx.lunar.server.world.World;

/**
 * Represents a basic player entity within a world or lobby.
 */
public class LunarPlayer extends LunarEntity {

    protected LunarServer server;
    protected AbstractConnection connection;

    // if this player has been loaded client-side into the world.
    protected boolean isLoaded;

    public LunarPlayer(Entity entity, boolean initializeComponents, LunarServer server, AbstractConnection connection) {
        super(entity, initializeComponents);
        this.server = server;
        this.connection = connection;
    }

    public LunarPlayer(boolean initializeComponents, LunarServer server, AbstractConnection connection) {
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
    public AbstractConnection getConnection() {
        return connection;
    }

    /**
     * @return the world the player is in.
     */
    public World getWorld() {
        return worldIn;
    }

    /**
     * @return {@code true} if this player is in a world.
     */
    public boolean inWorld() {
        return worldIn != null;
    }

    /**
     * @param worldIn world in
     */
    public void setWorldIn(World worldIn) {
        this.worldIn = worldIn;
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
    public void sendPlayerToOtherPlayer(LunarPlayer other) {
        other.getConnection().sendImmediately(new SPacketCreatePlayer(entityName, entityId, getPosition().x, getPosition().y));
    }

    @Override
    public void dispose() {
        isLoaded = false;
    }
}
