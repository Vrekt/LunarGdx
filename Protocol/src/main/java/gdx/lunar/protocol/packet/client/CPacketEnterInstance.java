package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public class CPacketEnterInstance extends Packet {

    public static final int PID = 8812;

    protected int instanceId;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleEnterInstance(new CPacketEnterInstance(buf));
    }

    public CPacketEnterInstance(int instanceId) {
        this.instanceId = instanceId;
    }

    private CPacketEnterInstance(ByteBuf buffer) {
        super(buffer);
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(instanceId);
    }

    @Override
    public void decode() {
        instanceId = buffer.readInt();
    }

}
