package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import io.netty.buffer.ByteBuf;

public class TestCustomJoinWorldPacket extends CPacketJoinWorld {

    private boolean testField;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        Gdx.app.log("Testing", "Hello From handle custom");
        handler.handleJoinWorld(new TestCustomJoinWorldPacket(buf));
    }

    public TestCustomJoinWorldPacket(String worldName, String username) {
        super(worldName, username);
    }

    public TestCustomJoinWorldPacket(ByteBuf buffer) {
        super(buffer);
    }

    public void setTestField(boolean testField) {
        this.testField = testField;
    }

    public boolean isTestField() {
        return testField;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
        writeString(username);
        buffer.writeBoolean(testField);
    }

    @Override
    public void decode() {
        worldName = readString();
        username = readString();
        testField = buffer.readBoolean();
    }
}
