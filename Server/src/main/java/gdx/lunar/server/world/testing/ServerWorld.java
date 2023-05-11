package gdx.lunar.server.world.testing;

import gdx.lunar.server.entity.LunarServerEntity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;

import java.util.concurrent.ConcurrentMap;

/**
 * Represents a server world that handles the list of players and entities
 *
 * @param <P> a player type
 * @param <E> an entity type
 */
public interface ServerWorld<P extends LunarServerPlayerEntity, E extends LunarServerEntity> extends World {

    /**
     * Add a player to the players list
     *
     * @param player the player
     */
    void addPlayer(P player);

    /**
     * Add an entity to the entities list
     *
     * @param entity the entity
     */
    void addEntity(E entity);

    /**
     * @return a list of all players in this world
     */
    ConcurrentMap<Integer, P> getPlayers();

    /**
     * @return a list of all entities in this world
     */
    ConcurrentMap<Integer, E> getEntities();

}
