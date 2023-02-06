package gdx.lunar.instance.config;

import gdx.lunar.world.WorldConfiguration;

/**
 * Configuration for {@link gdx.lunar.instance.LunarInstance}
 */
public class InstanceConfiguration extends WorldConfiguration {

    // if the parent world should still be updated while in this instance
    public boolean unloadParentWorld, updateParentWorld;

    // represents how long after entering the instance before the parent world stops updating
    // only valid if {@updateParentWorld} = true AND {@unloadParentWorld} = false
    public float stopUpdatingParentWorldAfter = 5000;
    // represents how long from the instance ENTRANCE the player needs to travel before parent world is stopped updating
    // only valid if {@updateParentWorld} = true AND {@unloadParentWorld} = false
    public float stopUpdatingParentWorldAfterDistance = 10;

    // if the instance should be unloaded (disposed) when leaving.
    public boolean unloadInstanceAfterLeaving = true;
    // when to unload the instance after exiting
    // 5000 = player has to be in parent world for 5 seconds before instance disposes
    public float unloadInstanceAfterInterval = 5000;
    // how far away the player needs to be before instance is disposed
    public float unloadInstanceAfterDistance = 10;

    // if the instance should be loaded in when player gets within X distance
    public boolean preLoadInstance = true;
    // if player is within this distance interior will be loaded provided {@preLoadInstance} = true;
    public float preLoadInstanceDistance = 10;

}
