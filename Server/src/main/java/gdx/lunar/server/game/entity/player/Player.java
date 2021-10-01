package gdx.lunar.server.game.entity.player;

import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketDisconnect;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.entity.Entity;
import gdx.lunar.server.network.PlayerConnection;
import gdx.lunar.server.world.World;

/**
 * Represents a player entity
 */
public class Player extends Entity {

    private final LunarServer server;
    private final PlayerConnection connection;

    protected World worldIn;

    protected boolean loaded;
    protected boolean inLobby;

    /**
     * Initialize
     *
     * @param entityId   ID
     * @param server     the server.
     * @param connection the connection
     */
    public Player(int entityId, LunarServer server, PlayerConnection connection) {
        super(entityId);
        this.server = server;
        this.connection = connection;
    }

    /**
     * Send this player to another player.
     */
    public void sendTo(Player other) {
        other.getConnection().send(new SPacketCreatePlayer(other.connection.alloc(), entityName, entityId, getX(), getY()));
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
    public PlayerConnection getConnection() {
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
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Kick this player
     *
     * @param reason the reason
     */
    public void kick(String reason) {
        server.handlePlayerDisconnect(this);

        connection.send(new SPacketDisconnect(connection.alloc(), reason));
        connection.disconnect();
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {

    }
}
