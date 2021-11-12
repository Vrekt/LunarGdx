package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.CPacketApplyEntityBodyForce;
import io.netty.buffer.ByteBuf;

/**
 * A broadcasted packet built from {@link CPacketApplyEntityBodyForce}
 */
public class SPacketApplyEntityBodyForce extends Packet {

    public static final int PID = 998;

    private int entityId;
    private float forceX, forceY, pointX, pointY;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleEntityBodyForce(new SPacketApplyEntityBodyForce(buf));
    }

    public SPacketApplyEntityBodyForce(CPacketApplyEntityBodyForce other) {
        this.entityId = other.getEntityId();
        this.forceX = other.getForceX();
        this.forceY = other.getForceY();
        this.pointX = other.getPointX();
        this.pointY = other.getPointY();
    }

    public SPacketApplyEntityBodyForce(int entityId, float forceX, float forceY, float pointX, float pointY) {
        this.entityId = entityId;
        this.forceX = forceX;
        this.forceY = forceY;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public SPacketApplyEntityBodyForce(ByteBuf buffer) {
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
