package gdx.lunar.protocol.packet.client;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

public class C2SPacketPlayerVelocity extends GamePacket {

    public static final int PACKET_ID = 2227;

    protected float velocityX, velocityY, rotation;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handlePlayerVelocity(new C2SPacketPlayerVelocity(buffer));
    }

    public C2SPacketPlayerVelocity(float velocityX, float velocityY, float rotation) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    public C2SPacketPlayerVelocity(Vector2 velocity, float rotation) {
        this(velocity.x, velocity.y, rotation);
    }

    private C2SPacketPlayerVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return x vel
     */
    public float getVelocityX() {
        return velocityX;
    }

    /**
     * @return y vel
     */
    public float getVelocityY() {
        return velocityY;
    }

    /**
     * @return the rotation value
     */
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
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
        buffer.writeFloat(rotation);
    }

    @Override
    public void decode() {
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
        rotation = buffer.readFloat();
    }
}
