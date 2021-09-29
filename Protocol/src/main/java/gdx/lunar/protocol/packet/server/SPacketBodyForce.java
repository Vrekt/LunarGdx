package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.CPacketBodyForce;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * A broadcasted packet built from {@link gdx.lunar.protocol.packet.client.CPacketBodyForce}
 */
public class SPacketBodyForce extends Packet {

    public static final int PID = 15;

    private int entityId;
    private float forceX, forceY, pointX, pointY;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleBodyForce(new SPacketBodyForce(buf));
    }

    public SPacketBodyForce(ByteBufAllocator allocator, CPacketBodyForce other) {
        super(allocator);
        this.entityId = other.getEntityId();
        this.forceX = other.getForceX();
        this.forceY = other.getForceY();
        this.pointX = other.getPointX();
        this.pointY = other.getPointY();
    }

    public SPacketBodyForce(ByteBufAllocator allocator, int entityId, float forceX, float forceY, float pointX, float pointY) {
        super(allocator);
        this.entityId = entityId;
        this.forceX = forceX;
        this.forceY = forceY;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public SPacketBodyForce(ByteBuf buffer) {
        super(buffer);
    }

    public int getEntityId() {
        return entityId;
    }

    public float getForceX() {
        return forceX;
    }

    public float getForceY() {
        return forceY;
    }

    public float getPointX() {
        return pointX;
    }

    public float getPointY() {
        return pointY;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeFloat(forceX);
        buffer.writeFloat(forceY);
        buffer.writeFloat(pointX);
        buffer.writeFloat(pointY);
    }

    @Override
    public void decode() {
        this.entityId = buffer.readInt();
        this.forceX = buffer.readFloat();
        this.forceY = buffer.readFloat();
        this.pointX = buffer.readFloat();
        this.pointY = buffer.readFloat();
    }

}
