package gdx.lunar.world;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a 'skeleton' of a main networked world. Contains just basic information about players and entities
 *
 * @param <N> A network player
 * @param <E> A network entity
 */
public abstract class AbstractWorld<N extends LunarNetworkEntityPlayer, E extends LunarEntity> extends ScreenAdapter implements Disposable {

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

    public void renderWorld(SpriteBatch batch) {

    }

    public void renderWorld(SpriteBatch batch, float delta) {

    }

    /**
     * Spawn the entity in this world
     * This method is to be called AFTER {@code LunarEntity#spawnEntityInWorld} (usually by default)
     * This method does not handle actually creating the new box2d entity
     *
     * @param entity the entity
     * @param x      location X
     * @param y      location Y
     */
    public void spawnEntityInWorld(LunarEntity entity, float x, float y) {
        addEntityToCollection(entity);
        entity.getPosition().set(x, y);
    }

    /**
     * This method will spawn the entity in this world at the spawn location
     *
     * @param entity the entity
     */
    public void spawnEntityInWorld(LunarEntity entity) {
        addEntityToCollection(entity);

        entity.getPosition().set(spawn.x, spawn.y);
    }

    /**
     * Add an entity to the internal lists
     *
     * @param entity entity
     */
    public void addEntityToCollection(LunarEntity entity) {
        if (entity instanceof LunarNetworkEntityPlayer) {
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
    public void removeEntityInWorld(LunarEntity entity) {
        if (entity instanceof LunarNetworkEntityPlayer) {
            this.players.remove(entity.getProperties().entityId);
        } else if (entity != null) {
            this.entities.remove(entity.getProperties().entityId);
        }
    }

    /**
     * Remove the entity from this world
     *
     * @param entity   the entity
     * @param isPlayer if the entity is a player
     */
    public void removeEntityInWorld(int entity, boolean isPlayer) {
        if (isPlayer) {
            this.players.remove(entity);
        } else {
            this.entities.remove(entity);
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
        players.clear();
        entities.clear();
    }
}
