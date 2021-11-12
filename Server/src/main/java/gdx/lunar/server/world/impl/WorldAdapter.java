package gdx.lunar.server.world.impl;

import gdx.lunar.protocol.packet.server.SPacketApplyEntityBodyForce;
import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import gdx.lunar.server.game.entity.player.LunarPlayer;
import gdx.lunar.server.game.entity.position.EntityVelocityComponent;
import gdx.lunar.server.world.World;
import gdx.lunar.server.world.config.WorldConfiguration;

/**
 * Represents a basic world with packet processing and a default {@link gdx.lunar.server.world.config.WorldConfiguration}
 */
public class WorldAdapter extends World {

    public WorldAdapter(String worldName, WorldConfiguration configuration) {
        super(worldName, configuration);
    }

    public WorldAdapter() {
        super("WorldAdapter", new WorldConfiguration());
    }

    @Override
    public void tick() {
        final long now = System.currentTimeMillis();

        for (LunarPlayer player : players.values()) {
            // flush anything that was sent to the player
            player.getConnection().flush();
            if (!checkPlayerTimeout(now, player)) {
                queuePlayerPosition(player);
                queuePlayerVelocity(player);
                queuePlayerForce(player);
            }
        }
    }

    private boolean checkPlayerTimeout(long now, LunarPlayer player) {
        if (now - player.getConnection().getLastPacketReceived() >= configuration.getPlayerTimeoutMs()) {
            player.getConnection().disconnect();
            return true;
        }
        return false;
    }

    private void queuePlayerPosition(LunarPlayer player) {
        broadcast(player.getEntityId(),
                new SPacketPlayerPosition(player.getEntityId(), player.getRotation(), player.getPosition().x, player.getPosition().y));
    }

    private void queuePlayerVelocity(LunarPlayer player) {
        broadcast(player.getEntityId(),
                new SPacketPlayerVelocity(player.getEntityId(), player.getRotation(), player.getPosition().x, player.getPosition().y));
    }

    private void queuePlayerForce(LunarPlayer player) {
        if (player.getVelocityComponent().hasForceApplied) {
            final EntityVelocityComponent component = player.getVelocityComponent();
            broadcast(player.getEntityId(), new SPacketApplyEntityBodyForce(player.getEntityId(),
                    component.force.x, component.force.y, component.point.x, component.point.y));
        }
    }

}
