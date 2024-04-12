package gdx.lunar.server.entity;

import gdx.lunar.server.network.connection.ServerAbstractConnection;

/**
 * Represents a player entity within the server.
 */
public interface ServerPlayerEntity extends ServerEntity {

    /**
     * @return the connection for this player
     */
    ServerAbstractConnection getConnection();

    /**
     * If the player has loaded into a world and is playable.
     *
     * @param isLoaded isLoaded
     */
    void setIsLoaded(boolean isLoaded);

    /**
     * @return {@code true} if this player is loaded
     */
    boolean isLoaded();

    /**
     * Kick this player
     *
     * @param reason the given reason
     */
    void kick(String reason);

}
