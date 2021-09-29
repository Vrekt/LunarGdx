package gdx.lunar.server.world.impl;

import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;

/**
 * A basic game world.
 */
public final class BasicLunarWorld extends World {

    private int statistics;
    private int total;

    public BasicLunarWorld() {
        super("LunarWorld", 100, 200);
    }

    @Override
    public void tick() {
        int packets = 0;

        // flush previous tick packets.
        for (Player value : players.values()) value.getConnection().flush();

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
