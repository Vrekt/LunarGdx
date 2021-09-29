package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Indicates this client is ready to receive world data.
 */
public class CPacketWorldLoaded extends Packet {

    public static final int PID = 13;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleWorldLoaded(new CPacketWorldLoaded());
    }

    public CPacketWorldLoaded(ByteBufAllocator allocator) {
        super(allocator);
    }

    private CPacketWorldLoaded() {
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
