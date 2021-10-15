package gdx.lunar;

import com.badlogic.gdx.Gdx;
import gdx.lunar.entity.player.prop.PlayerProperties;

/**
 * The base lunar handler.
 */
public final class Lunar {

    public static int protocolVersion = 1;
    public static String gameVersion = "lunar-game-1";

    private PlayerProperties playerProperties;
    private static boolean gdxInitialized;

    public void setPlayerProperties(PlayerProperties playerProperties) {
        this.playerProperties = playerProperties;
    }

    public PlayerProperties getPlayerProperties() {
        return playerProperties;
    }

    public void setUseGdxLogging(boolean gdx) {
        gdxInitialized = gdx;
    }

    public static void log(String tag, String message) {
        if (gdxInitialized) {
            Gdx.app.log(tag, message);
        }
    }

}
