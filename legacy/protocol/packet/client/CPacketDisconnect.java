package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A packet that indicates the client disconnected.
 */
public class CPacketDisconnect extends Packet {

    public static final int PID = 882;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleDisconnect(new CPacketDisconnect());
    }

    public CPacketDisconnect() {
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
    }

}
