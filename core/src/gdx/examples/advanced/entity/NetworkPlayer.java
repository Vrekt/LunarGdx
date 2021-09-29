package gdx.examples.advanced.entity;

import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarNetworkPlayer;

/**
 * Here I could define some custom behaviour or just simply expand.
 */
public class NetworkPlayer extends LunarNetworkPlayer {

    protected boolean myCustomProperty;

    public NetworkPlayer(int entityId) {
        super(entityId, (1 / 16.0f), 16, 18, Rotation.FACING_UP);
    }

    public boolean myCustomProperty() {
        return myCustomProperty;
    }

    public void setMyCustomProperty(boolean myCustomProperty) {
        this.myCustomProperty = myCustomProperty;
    }
}
