package gdx.lunar.server.world.testing;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import gdx.lunar.server.entity.LunarServerEntity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a {@link ServerWorld} that can be expanded upon
 */
public abstract class AbstractServerWorld<P extends LunarServerPlayerEntity, E extends LunarServerEntity> implements ServerWorld<P, E> {

    // network players and entities
    protected ConcurrentMap<Integer, P> players = new ConcurrentHashMap<>();
    protected ConcurrentMap<Integer, E> entities = new ConcurrentHashMap<>();

    // starting/spawn point of this world.
    protected final Vector2 spawn = new Vector2();

    protected final ServerWorldConfiguration configuration;
    protected final String worldName;

    public AbstractServerWorld(ServerWorldConfiguration configuration, String worldName) {
        this.configuration = configuration;
        this.worldName = worldName;
    }

    @Override
    public String getName() {
        return worldName;
    }

    @Override
    public boolean isFull() {
        return players.size() >= configuration.getCapacity();
    }

    @Override
    public <T extends LunarServerPlayerEntity> boolean isTimedOut(T player, float now) {
        return now - player.getServerConnection().getLastPacketReceived() >= configuration.getPlayerTimeoutMs();
    }

    @Override
    public <T extends LunarServerPlayerEntity> void timeoutPlayer(T player) {
        player.kickPlayer("Timed out.");

        removePlayerInWorld(player);
        player.getServer().handlePlayerDisconnect(player);
    }

    @Override
    public boolean doesUsernameExist(String username) {
        return players.values().stream().anyMatch(player -> player.getName().equals(username));
    }

    @Override
    public int assignEntityIdFor(boolean isPlayer) {
        if (isPlayer) {
            return players.size() + 1 + ThreadLocalRandom.current().nextInt(111, 999);
        } else {
            return entities.size() + 1 + ThreadLocalRandom.current().nextInt(111, 999);
        }
    }

    @Override
    public boolean hasPlayer(int entityId) {
        return players.containsKey(entityId);
    }

    @Override
    public boolean hasEntity(int entityId) {
        return entities.containsKey(entityId);
    }

    @Override
    public void addPlayer(P player) {
        this.players.put(player.getEntityId(), player);
    }

    @Override
    public void addEntity(E entity) {
        this.entities.put(entity.getEntityId(), entity);
    }

    @Override
    public ConcurrentMap<Integer, P> getPlayers() {
        return players;
    }

    @Override
    public ConcurrentMap<Integer, E> getEntities() {
        return entities;
    }

    @Override
    public <T extends LunarServerPlayerEntity> void handlePlayerPosition(T player, float x, float y, float angle) {
        player.setPosition(x, y, true);
        player.setRotation(angle);
    }

    @Override
    public <T extends LunarServerPlayerEntity> void handlePlayerVelocity(T player, float x, float y, float angle) {
        player.getVelocity().set(x, y);
        player.setRotation(angle);
    }

    @Override
    public <T extends LunarServerPlayerEntity> void spawnPlayerInWorld(T player) {
        player.setWorldIn(this);
        player.setPosition(0.0f, 0.0f, true);

        // send all current players in this world to the connecting player.
        for (LunarServerPlayerEntity other : players.values()) other.sendPlayerToPlayer(player);

        // broadcast the joining of this player to others.
        broadcastNowWithExclusion(player.getEntityId(), new SPacketCreatePlayer(player.getName(), player.getEntityId(), 0.0f, 0.0f));

        // add this new player to the list
        players.put(player.getEntityId(), (P) player);
    }

    @Override
    public <T extends LunarServerPlayerEntity> void removePlayerInWorld(T player) {
        if (!hasPlayer(player.getEntityId())) return;
        players.remove(player.getEntityId());

        broadcastNowWithExclusion(player.getEntityId(), new SPacketRemovePlayer(player.getEntityId()));
    }

    @Override
    public <T extends LunarServerPlayerEntity> void removeEntityInWorld(T entity) {
        entities.remove(entity.getEntityId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LunarServerPlayerEntity> T getPlayer(int entityId) {
        return (T) players.get(entityId);
    }

    @Override
    public void broadcast(Packet packet) {
        for (LunarServerPlayerEntity player : players.values()) player.getServerConnection().queue(packet);
    }

    @Override
    public void broadcastNowWithExclusion(int exclusion, Packet packet) {
        for (LunarServerPlayerEntity player : players.values())
            if (player.getEntityId() != exclusion) player.getServerConnection().sendImmediately(packet);
    }


    @Override
    public void broadcastWithExclusion(int exclusion, Packet packet) {
        for (LunarServerPlayerEntity value : players.values()) {
            if (value.getEntityId() != exclusion) {
                value.getServerConnection().queue(packet);
            }
        }
    }

    @Override
    public void tick(float delta) {
        if (configuration.doDefaultTicking) {
            for (LunarServerPlayerEntity player : players.values()) {
                // flush anything that was sent to the player
                player.getServerConnection().flush();
                final long now = System.currentTimeMillis();

                if (!isTimedOut(player, now)) {
                    queuePlayerPosition(player);
                    queuePlayerVelocity(player);
                } else {
                    timeoutPlayer(player);
                    player.dispose();
                }
            }
        }
    }

    /**
     * Queue a player position update
     *
     * @param player the player
     */
    protected void queuePlayerPosition(LunarServerPlayerEntity player) {
        broadcastWithExclusion(player.getEntityId(),
                new SPacketPlayerPosition(player.getEntityId(), player.getRotation(), player.getPosition().x, player.getPosition().y));
    }

    /**
     * Queue a player velocity update
     *
     * @param player the player
     */
    private void queuePlayerVelocity(LunarServerPlayerEntity player) {
        broadcastWithExclusion(player.getEntityId(),
                new SPacketPlayerVelocity(player.getEntityId(), player.getRotation(), player.getVelocity().x, player.getVelocity().y));
    }

    @Override
    public void dispose() {
        players.values().forEach(LunarServerPlayerEntity::dispose);
        players.clear();
        entities.values().forEach(LunarServerEntity::dispose);
        entities.clear();
    }
}
