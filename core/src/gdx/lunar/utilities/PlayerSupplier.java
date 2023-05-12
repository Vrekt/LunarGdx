package gdx.lunar.utilities;

import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Provides game worlds with your player context
 */
public interface PlayerSupplier {

    /**
     * @param <T> player type
     * @return your local player
     */
    <T extends LunarEntityPlayer> T getPlayer();

}
