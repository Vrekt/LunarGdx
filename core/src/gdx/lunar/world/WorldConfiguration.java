package gdx.lunar.world;

/**
 * A basic configuration for {@link LunarWorld}
 */
public class WorldConfiguration {

    // world properties
    public boolean handlePhysics = true, updatePlayer = true, updateNetworkPlayers = true;
    public boolean updateEntities = true;
    public boolean updateEngine = true;

    // physics
    protected int velocityIterations = 8, positionIterations = 3;
    protected float stepTime = 1.0f / 60.0f;
    protected float maxFrameTime = 0.25f;

    protected float worldScale;

}
