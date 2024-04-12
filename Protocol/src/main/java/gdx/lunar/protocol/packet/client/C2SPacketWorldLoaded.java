package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Notify the server this client has loaded the world.
 */
public class C2SPacketWorldLoaded extends GamePacket {

    public static final int PACKET_ID = 2225;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handleWorldLoaded(new C2SPacketWorldLoaded());
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
    }
}
