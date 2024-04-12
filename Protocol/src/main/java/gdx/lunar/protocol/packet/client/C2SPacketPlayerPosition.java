package gdx.lunar.protocol.packet.client;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

public class C2SPacketPlayerPosition extends GamePacket {

    public static final int PACKET_ID = 2226;

    protected float rotation, x, y;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handlePlayerPosition(new C2SPacketPlayerPosition(buffer));
    }

    public C2SPacketPlayerPosition(float x, float y, float rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public C2SPacketPlayerPosition(Vector2 position, float rotation) {
        this(position.x, position.y, rotation);
    }

    private C2SPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRotation() {
        return rotation;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(rotation);
    }

    @Override
    public void decode() {
        x = buffer.readFloat();
        y = buffer.readFloat();
        rotation = buffer.readFloat();
    }
}
