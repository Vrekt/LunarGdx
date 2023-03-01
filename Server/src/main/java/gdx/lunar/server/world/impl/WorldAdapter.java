package gdx.lunar.server.world.impl;

import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.world.ServerWorld;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

/**
 * Represents a basic world with packet processing and a default {@link ServerWorldConfiguration}
 */
public class WorldAdapter extends ServerWorld {

    public WorldAdapter(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

    public WorldAdapter() {
        super(new ServerWorldConfiguration(), "WorldAdapter");
    }

    @Override
    public void tick() {
        for (LunarServerPlayerEntity player : players.values()) {
            // flush anything that was sent to the player
            player.getServerConnection().flush();
            final long now = System.currentTimeMillis();

            if (!isTimedOut(player, now)) {
                queuePlayerPosition(player);
                queuePlayerVelocity(player);
                queuePlayerForce(player);
            } else {
                timeoutPlayer(player);
                player.dispose();
            }
        }
    }

    private void queuePlayerPosition(LunarServerPlayerEntity player) {
        broadcast(player.getEntityId(),
                new SPacketPlayerPosition(player.getEntityId(), player.getRotation(), player.getPosition().x, player.getPosition().y));
    }

    private void queuePlayerVelocity(LunarServerPlayerEntity player) {
        broadcast(player.getEntityId(),
                new SPacketPlayerVelocity(player.getEntityId(), player.getRotation(), player.getVelocity().x, player.getVelocity().y));
    }

    private void queuePlayerForce(LunarServerPlayerEntity player) {
        // TODO?
    }

}
