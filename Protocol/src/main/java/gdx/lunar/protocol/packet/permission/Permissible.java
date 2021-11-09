package gdx.lunar.protocol.packet.permission;

import gdx.lunar.protocol.packet.Packet;

/**
 * A packet that requires permission
 */
public interface Permissible {

    Packet requester();

    boolean hasPermission();

    void setPermission(boolean permission);

}
