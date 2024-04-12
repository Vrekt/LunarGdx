package gdx.lunar.world.instance;

import gdx.lunar.world.LunarWorld;

/**
 * Represents an instance within a world, for example interiors, dungeons, ,etc,
 * where the player can exit and go back to the original world
 */
public interface LunarInstance {

    /**
     * @return the name of this instance
     */
    String getInstanceName();

    /**
     * Set the name of this instance
     *
     * @param instanceName the instance name
     */
    void setInstanceName(String instanceName);

    boolean isEnterable();

    void setEnterable(boolean enterable);

    void enter();

    LunarWorld getWorldIn();

    void setWorldIn(LunarWorld world);

}
