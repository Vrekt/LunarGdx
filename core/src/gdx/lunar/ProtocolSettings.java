package gdx.lunar;

/**
 * Game and current protocol version is stored here.
 * This is for handling outdated clients, protocol or game.
 */
public final class ProtocolSettings {

    public static int protocolVersion = 1;
    public static String gameVersion = "1.0";

    private ProtocolSettings() {
        throw new UnsupportedOperationException();
    }
}
