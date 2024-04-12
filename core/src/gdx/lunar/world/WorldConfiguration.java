package gdx.lunar.world;

/**
 * A basic configuration for {@link LunarWorld}
 */
public class WorldConfiguration {

    // world properties
    public boolean handlePhysics = true, updateLocalPlayer = true, updateNetworkPlayers = true;
    public boolean updateEntities = true;
    public boolean updateEntityEngine = true;
    public int maxPlayerCapacity = 100;

    // physics
    public int velocityIterations = 8, positionIterations = 3;
    public float stepTime = 1.0f / 60.0f;
    public float maxFrameTime = 0.25f;

    public float worldScale;

}
