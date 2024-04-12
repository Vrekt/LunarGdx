package gdx.lunar.world.instance;

import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.mp.LunarEntityNetworkPlayer;

/**
 * Represents a default implementation of an instance
 *
 * @param <P> player
 * @param <E> entity
 */
public abstract class AbstractGameInstance<P extends LunarEntityNetworkPlayer, E extends LunarEntity> implements TypedWorldInstance<P, E> {
}
