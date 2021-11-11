package gdx.examples.advanced.packet;

import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class MyCustomPositionPacketServer extends SPacketPlayerPosition {

    public MyCustomPositionPacketServer(ByteBufAllocator allocator, int entityId, int rotation, float x, float y) {
        super(allocator, entityId, rotation, x, y);
    }

    public MyCustomPositionPacketServer(ByteBuf buffer) {
        super(buffer);
    }
}
