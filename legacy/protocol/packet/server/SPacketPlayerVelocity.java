package gdx.lunar.protocol.packet.server;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to update a client velocity
 */
public class SPacketPlayerVelocity extends Packet {

    public static final int PID = 996;

    protected int entityId;
    protected float velocityX, velocityY, rotation;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerVelocity(new SPacketPlayerVelocity(buf));
    }

    public SPacketPlayerVelocity(int entityId, float rotation, float velocityX, float velocityY) {
        this.entityId = entityId;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    public SPacketPlayerVelocity(int entityId, float rotation, Vector2 velocity) {
        this(entityId, rotation, velocity.x, velocity.y);
    }

    private SPacketPlayerVelocity(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return EID
     */
    public int getEntityId() {
        return entityId;
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
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeFloat(velocityX);
        buffer.writeFloat(velocityY);
        buffer.writeFloat(rotation);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        velocityX = buffer.readFloat();
        velocityY = buffer.readFloat();
        rotation = buffer.readFloat();
    }

}
