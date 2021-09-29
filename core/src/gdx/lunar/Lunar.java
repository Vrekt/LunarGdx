package gdx.lunar;

import gdx.lunar.entity.player.prop.PlayerProperties;

/**
 * The base lunar handler.
 */
public final class Lunar {

    public static int protocolVersion = 1;
    public static String gameVersion = "lunar-game-1";

    private PlayerProperties playerProperties;

    public void setPlayerProperties(PlayerProperties playerProperties) {
        this.playerProperties = playerProperties;
    }

    public PlayerProperties getPlayerProperties() {
        return playerProperties;
    }
}
