package gdx.lunar.server.world;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.server.entity.LunarServerEntity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.utilities.Disposable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base world implementation for a server
 *
 * @param <N> ServerPlayerEntity type
 * @param <E> ServerEntity type
 */
public abstract class AbstractServerWorld<N extends LunarServerPlayerEntity, E extends LunarServerEntity> implements Disposable {

    // network players and entities
    protected ConcurrentMap<Integer, N> players = new ConcurrentHashMap<>();
    protected ConcurrentMap<Integer, E> entities = new ConcurrentHashMap<>();

    // starting/spawn point of this world.
    protected final Vector2 spawn = new Vector2();

    /**
     * @return network players in this world
     */
    public ConcurrentMap<Integer, N> getPlayers() {
        return players;
    }

    /**
     * @return entities in this world
     */
    public ConcurrentMap<Integer, E> getEntities() {
        return entities;
    }

    public Vector2 getSpawn() {
        return spawn;
    }

    public void setSpawn(Vector2 where) {
        this.spawn.set(where);
    }

    public void setSpawn(float x, float y) {
        this.spawn.set(x, y);
    }

    /**
     * Add an entity to the internal lists
     *
     * @param entity entity
     */
    public void addEntityToCollection(LunarServerEntity entity) {
        if (entity instanceof LunarServerPlayerEntity) {
            this.players.put(entity.getProperties().entityId, (N) entity);
        } else if (entity != null) {
            this.entities.put(entity.getProperties().entityId, (E) entity);
        }
    }

    /**
     * Remove the entity from this world
     *
     * @param entity the entity
     */
    public void removeEntityInWorld(LunarServerEntity entity) {
        if (entity instanceof LunarServerPlayerEntity) {
            this.players.remove(entity.getProperties().entityId);
        } else if (entity != null) {
            this.entities.remove(entity.getProperties().entityId);
        }
    }

    public boolean hasNetworkPlayer(int id) {
        return this.players.containsKey(id);
    }

    public N getNetworkPlayer(int id) {
        return this.players.get(id);
    }

    /**
     * Update this world
     *
     * @param d delta time.
     * @return capped frame time
     */
    public float update(float d) {
        return d;
    }

    @Override
    public void dispose() {
        players.values().forEach(LunarServerPlayerEntity::dispose);
        players.clear();
        entities.values().forEach(LunarServerEntity::dispose);
        entities.clear();
    }
}
