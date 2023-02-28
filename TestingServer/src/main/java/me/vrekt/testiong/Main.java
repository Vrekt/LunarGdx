package me.vrekt.testiong;

import gdx.examples.basic.TestCustomJoinWorldPacket;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import gdx.lunar.server.game.GameServer;
import gdx.lunar.server.instance.InstanceAdapter;
import gdx.lunar.server.netty.NettyServer;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.impl.WorldAdapter;

public class Main {

    public static void main(String[] args) {

        final LunarProtocol protocol = new LunarProtocol(true);
        final GameServer gameServer = new GameServer(protocol, "1.0");

        final NettyServer server = new NettyServer("localhost", 6969, protocol, gameServer);
        server.bind();
        server.setConnectionProvider(channel -> new Connection(channel, gameServer));

        protocol.changeDefaultClientPacketHandlerFor(CPacketJoinWorld.PID, (buf, handler) -> {
            final TestCustomJoinWorldPacket packet = new TestCustomJoinWorldPacket(buf);
            handler.handleJoinWorld(packet);
        });

        gameServer.getWorldManager().addWorld("TutorialWorld", new WorldAdapter(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.getWorldManager().getWorld("TutorialWorld").addInstance(new InstanceAdapter(new ServerWorldConfiguration(), "Testing123", 22));
        gameServer.start();

        System.err.println("Server running.");
    }

}
