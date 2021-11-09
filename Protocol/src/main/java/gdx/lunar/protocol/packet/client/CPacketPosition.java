package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by clients to update their position
 */
public class CPacketPosition extends Packet {

    public static final int PID = 9;

    private float rotation, x, y;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerPosition(new CPacketPosition(buf));
    }

    public CPacketPosition(float rotation, float x, float y) {
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
    public float getRotation() {
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
        buffer.writeFloat(rotation);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        rotation = buffer.readFloat();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }
}
