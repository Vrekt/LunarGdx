package gdx.lunar.world;

import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;

import java.util.concurrent.ConcurrentMap;

/**
 * Represents a game world that is typed for custom entities
 *
 * @param <P> player type
 * @param <E> entity type
 */
public interface TypedGameWorld<P extends LunarNetworkEntityPlayer, E extends LunarEntity> extends LunarWorld {

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
