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
        super(new ServerWorldConfiguration(), "TutorialWorld");
    }

    @Override
    public void tick() {
        final long now = System.currentTimeMillis();

        for (LunarServerPlayerEntity player : players.values()) {
            // flush anything that was sent to the player
            player.getServerConnection().flush();

            if (!checkPlayerTimeout(now, player)) {
                queuePlayerPosition(player);
                queuePlayerVelocity(player);
                queuePlayerForce(player);
            }
        }
    }

    private boolean checkPlayerTimeout(long now, LunarServerPlayerEntity player) {
        if (now - player.getServerConnection().getLastPacketReceived() >= configuration.getPlayerTimeoutMs()) {
            System.err.println("Timed out: " + player.getEntityId());
            //  player.getServerConnection().disconnect();
            return true;
        }
        return false;
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
        // TODO:
    }

}
