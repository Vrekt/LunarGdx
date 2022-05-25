package gdx.lunar.protocol.auth;

public enum AuthenticationMethod {

    /**
     * No authentication, anybody can join.
     */
    NONE,
    /**
     * Verify username is valid and doesn't exist.
     */
    VERIFY_USERNAME,
    /**
     * Verify the world exists before joining.
     */
    VERIFY_WORLD_EXISTS

}
