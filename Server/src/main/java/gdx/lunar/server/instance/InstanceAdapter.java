package gdx.lunar.server.instance;

import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

public class InstanceAdapter extends Instance{

    public InstanceAdapter(ServerWorldConfiguration configuration, String worldName, int instanceId) {
        super(configuration, worldName, instanceId);
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
        // TODO:
    }

}
