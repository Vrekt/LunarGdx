package gdx.examples.advanced.packet;

import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class MyCustomPacket extends Packet {

    public MyCustomPacket(ByteBufAllocator allocator) {
        super(allocator);
    }

    public MyCustomPacket(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return 99;
    }
}
