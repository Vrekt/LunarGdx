package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A client packet for when the players body was given a force to it.
 */
public class CPacketApplyEntityBodyForce extends Packet {

    public static final int PID = 887;

    protected int entityId;
    protected float forceX, forceY, pointX, pointY;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleBodyForce(new CPacketApplyEntityBodyForce(buf));
    }

    public CPacketApplyEntityBodyForce(int entityId, float forceX, float forceY, float pointX, float pointY) {
        this.entityId = entityId;
        this.forceX = forceX;
        this.forceY = forceY;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public CPacketApplyEntityBodyForce(ByteBuf buffer) {
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
