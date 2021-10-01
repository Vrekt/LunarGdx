package gdx.lunar.server;

import gdx.lunar.server.game.entity.player.Player;

/**
 * The lunar (game) server.
 */
public final class LunarGameServer extends LunarServer {

    @Override
    public void handlePlayerDisconnect(Player player) {
        super.handlePlayerDisconnect(player);

        if (player.getWorld() != null) {
            player.getWorld().removePlayerInWorld(player);
        }
    }
}
