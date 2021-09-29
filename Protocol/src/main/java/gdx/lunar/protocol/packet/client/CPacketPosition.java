package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Sent by clients to update their position
 */
public class CPacketPosition extends Packet {

    public static final int PID = 9;

    /**
     * Rotation
     */
    private int rotation;

    /**
     * Position
     */
    private float x, y;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerPosition(new CPacketPosition(buf));
    }

    public CPacketPosition(ByteBufAllocator allocator, int rotation, float x, float y) {
        super(allocator);
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    private CPacketPosition(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return rotation index
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * @return x
     */
    public float getX() {
        return x;
    }

    /**
     * @return y
     */
    public float getY() {
        return y;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        rotation = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}
