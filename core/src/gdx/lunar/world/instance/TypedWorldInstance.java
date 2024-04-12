package gdx.lunar.world.instance;

import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.mp.LunarEntityNetworkPlayer;

import java.util.concurrent.ConcurrentMap;

public interface TypedWorldInstance<P extends LunarEntityNetworkPlayer, E extends LunarEntity> extends LunarInstance {

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
     * @return a list of all players in this instance
     */
    ConcurrentMap<Integer, P> getPlayers();

    /**
     * @return a list of all entities in this instance
     */
    ConcurrentMap<Integer, E> getEntities();

}
