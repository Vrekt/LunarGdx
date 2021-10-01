package gdx.lunar.world.lobby;

import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.world.LunarWorldAdapter;

/**
 * A world that can be used as a game lobby.
 * <p>
 * This provides no extra functionality.
 */
public class GameLobbyWorldAdapter extends LunarWorldAdapter {

    public GameLobbyWorldAdapter(LunarEntityPlayer player, World world, float worldScale) {
        super(player, world, worldScale, true, true, true, true);
    }
}
