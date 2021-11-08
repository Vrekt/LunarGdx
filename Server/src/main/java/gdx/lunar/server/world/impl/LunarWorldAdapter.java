package gdx.lunar.server.world.impl;

import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;

/**
 * Represents a basic world with packet processing and a player capacity of 100, 100 max entities, and 1 entity request per second.
 */
public class LunarWorldAdapter extends World {

    private int statistics;
    private int total;

    public LunarWorldAdapter(String worldName, int maxPacketsPerTick, int capacity, int maxEntities, int maxEntityRequests) {
        super(worldName, maxPacketsPerTick, capacity, maxEntities, maxEntityRequests);
    }

    public LunarWorldAdapter() {
        super("LunarWorld", 100, 200, 100, 1);

        setAllowedToSpawnEntities(true);
        setPlayerTimeoutMs(3000);
    }

    @Override
    public void tick() {
        int packets = 0;

        // flush previous tick packets.
        final long now = System.currentTimeMillis();
        for (Player value : players.values()) {
            value.getConnection().flush();

            // update timed out players.
            if (System.currentTimeMillis() - value.getConnection().getLastPacketReceived()
                    >= 3000) {

                // time out.
                if (value.getWorld() != null) {
                    value.getWorld().removePlayerInWorld(value);
                }
                value.getServer().handlePlayerDisconnect(value);
                value.getConnection().disconnect();
            }
        }

        while (queuedPackets.peek() != null) {
            final QueuedPacket packet = queuedPackets.poll();
            broadcast(packet.entityId, packet.packet);

            if (packets++ >= maxPacketsPerTick) break;
        }


        total += packets;

        statistics++;
        if (statistics >= 400) {
            statistics = 0;

            System.err.println("Wrote " + packets + " packets this tick and " + total + " packets all time.");
        }
    }
}
