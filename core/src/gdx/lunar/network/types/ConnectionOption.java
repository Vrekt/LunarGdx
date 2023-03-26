package gdx.lunar.network.types;

/**
 * Disable and enable options within {@link PlayerConnectionHandler}
 */
public enum ConnectionOption {

    HANDLE_PLAYER_JOIN,
    HANDLE_PLAYER_LEAVE,
    HANDLE_PLAYER_VELOCITY,
    HANDLE_PLAYER_POSITION,
    HANDLE_PLAYER_FORCE,
    HANDLE_DISCONNECT,
    HANDLE_AUTHENTICATION,
    HANDLE_JOIN_WORLD,
    HANDLE_SET_ENTITY_PROPERTIES,
    HANDLE_WORLD_INVALID,
    HANDLE_JOIN_INSTANCE,
    PING


}
