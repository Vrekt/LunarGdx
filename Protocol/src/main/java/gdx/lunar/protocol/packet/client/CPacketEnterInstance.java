package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Clients request to enter an instance
 */
public class CPacketEnterInstance extends Packet {

    // reserved for CPacketNetworkedTile = 27
    public static final int PID = 28;

    private String instanceName;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleEnterInstance(new CPacketEnterInstance(buf));
    }

    public CPacketEnterInstance(ByteBufAllocator allocator, String interiorName) {
        super(allocator);
        this.instanceName = interiorName;
    }

    public CPacketEnterInstance(ByteBuf buffer) {
        super(buffer);
    }

    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public void encode() {
        writeId();
        writeString(instanceName);
    }

    @Override
    public void decode() {
        instanceName = readString();
    }

    @Override
    public int getId() {
        return PID;
    }
}
