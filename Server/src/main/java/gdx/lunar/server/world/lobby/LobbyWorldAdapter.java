package gdx.lunar.server.world.lobby;

import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;

/**
 * Represents a basic game lobby with a max player capacity of 8 and no entity requests allowed.
 */
public class LobbyWorldAdapter extends World {

    public LobbyWorldAdapter() {
        super("WorldLobby", 100, 8, 0, 0);

        setAllowedToSpawnEntities(false);
        setPlayerTimeoutMs(5000);
    }

    @Override
    public void tick() {
        int packets = 0;

        // flush previous tick packets.
        for (Player player : players.values()) {
            player.getConnection().flush();

            // update timed out players.
            if (isTimedOut(player)) timeoutPlayer(player);
        }

        while (queuedPackets.peek() != null) {
            final QueuedPacket packet = queuedPackets.poll();
            broadcast(packet.entityId, packet.packet);
            if (packets++ >= maxPacketsPerTick) break;
        }
    }
}
