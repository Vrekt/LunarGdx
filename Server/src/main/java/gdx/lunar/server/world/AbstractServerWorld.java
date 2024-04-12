package gdx.lunar.server.world;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.server.entity.ServerEntity;
import gdx.lunar.server.entity.ServerPlayerEntity;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a {@link World} that can be expanded upon
 */
public abstract class AbstractServerWorld implements World {

    // network players and entities
    protected ConcurrentMap<Integer, ServerPlayerEntity> players = new ConcurrentHashMap<>();
    protected ConcurrentMap<Integer, ServerEntity> entities = new ConcurrentHashMap<>();

    // starting/spawn point of this world.
    protected final Vector2 spawn = new Vector2();

    protected final ServerWorldConfiguration configuration;
    protected final String worldName;

    protected long currentTime;
    protected float currentTick;

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
    public boolean isTimedOut(ServerPlayerEntity player, float now) {
        return now - player.getConnection().getLastPacketReceived() >= configuration.getPlayerTimeoutMs();
    }

    @Override
    public void timeoutPlayer(ServerPlayerEntity player) {
        player.kick("Timed out.");

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
    public ConcurrentMap<Integer, ServerPlayerEntity> getPlayers() {
        return players;
    }

    @Override
    public ConcurrentMap<Integer, ServerEntity> getEntities() {
        return entities;
    }

    @Override
    public void handlePlayerPosition(ServerPlayerEntity player, float x, float y, float rotation) {
        player.setPosition(x, y);
        player.setRotation(rotation);
    }

    @Override
    public void handlePlayerVelocity(ServerPlayerEntity player, float x, float y, float rotation) {
        player.setVelocity(x, y);
        player.setRotation(rotation);
    }

    @Override
    public void spawnPlayerInWorld(ServerPlayerEntity player) {
        player.setWorldIn(this);
        if (players.isEmpty()) {
            // no players, send empty start game
            player.getConnection().sendImmediately(new S2CPacketStartGame(currentTime));
        } else {
            final S2CPacketStartGame.BasicServerPlayer[] serverPlayers = new S2CPacketStartGame.BasicServerPlayer[players.size()];
            // first, notify other players a new player as joined
            broadcastNowWithExclusion(player.getEntityId(), new S2CPacketCreatePlayer(player.getName(), player.getEntityId(), 0.0f, 0.0f));

            // next, construct start game packet
            int index = 0;
            for (ServerPlayerEntity other : players.values()) {
                serverPlayers[index] = new S2CPacketStartGame.BasicServerPlayer(other.getEntityId(), other.getName(), other.getPosition());
                index++;
            }

            // send!
            player.getConnection().sendImmediately(new S2CPacketStartGame(currentTime, serverPlayers));
        }

        // add this new player to the list
        players.put(player.getEntityId(), player);
    }

    @Override
    public void spawnEntityInWorld(ServerEntity entity) {
        // TODO: Maybe something different depending on how complex entities get.
        this.entities.put(entity.getEntityId(), entity);
    }

    @Override
    public void removePlayerInWorld(ServerPlayerEntity player) {
        if (!hasPlayer(player.getEntityId())) return;
        players.remove(player.getEntityId());

        broadcastNowWithExclusion(player.getEntityId(), new S2CPacketRemovePlayer(player.getEntityId()));
    }

    @Override
    public void removeEntityInWorld(ServerEntity entity) {
        entities.remove(entity.getEntityId());
    }

    @Override
    public ServerPlayerEntity getPlayer(int entityId) {
        return players.get(entityId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ServerPlayerEntity> T getPlayerAs(int entityId) {
        return (T) players.get(entityId);
    }

    @Override
    public ServerEntity getEntity(int entityId) {
        return entities.get(entityId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ServerEntity> T getEntityAs(int entityId) {
        return (T) entities.get(entityId);
    }

    @Override
    public void broadcast(Packet packet) {
        for (ServerPlayerEntity player : players.values()) player.getConnection().queue(packet);
    }

    @Override
    public void broadcastNowWithExclusion(int exclusion, Packet packet) {
        for (ServerPlayerEntity player : players.values())
            if (player.getEntityId() != exclusion) player.getConnection().sendImmediately(packet);
    }


    @Override
    public void broadcastWithExclusion(int exclusion, Packet packet) {
        for (ServerPlayerEntity value : players.values()) {
            if (value.getEntityId() != exclusion) {
                value.getConnection().queue(packet);
            }
        }
    }

    @Override
    public void tick(float delta) {
        if (configuration.doDefaultTicking) {
            for (ServerPlayerEntity player : players.values()) {
                // flush anything that was sent to the player
                player.getConnection().flush();
                currentTime = System.currentTimeMillis();

                if (!isTimedOut(player, currentTime)) {
                    queuePlayerPosition(player);
                    queuePlayerVelocity(player);
                } else {
                    timeoutPlayer(player);
                    player.dispose();
                }
            }
        }
        // TODO: Maybe just ++1?
        // TODO: Really just depends on implementation
        currentTick += delta;
    }

    /**
     * Queue a player position update
     *
     * @param player the player
     */
    protected void queuePlayerPosition(ServerPlayerEntity player) {
        broadcastWithExclusion(player.getEntityId(), new S2CPacketPlayerPosition(player.getEntityId(), player.getRotation(), player.getPosition()));
    }

    /**
     * Queue a player velocity update
     *
     * @param player the player
     */
    private void queuePlayerVelocity(ServerPlayerEntity player) {
        broadcastWithExclusion(player.getEntityId(), new S2CPacketPlayerVelocity(player.getEntityId(), player.getRotation(), player.getVelocity()));
    }

    @Override
    public long getTime() {
        return currentTime;
    }

    @Override
    public float getTick() {
        return currentTick;
    }

    @Override
    public void dispose() {
        players.values().forEach(ServerPlayerEntity::dispose);
        players.clear();
        entities.values().forEach(ServerEntity::dispose);
        entities.clear();
    }
}
