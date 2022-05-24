package me.vrekt.testiong;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.GameServer;
import gdx.lunar.server.NettyServer;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.impl.WorldAdapter;

public class Main {

    public static void main(String[] args) {

        final LunarProtocol protocol = new LunarProtocol(true);
        final GameServer gameServer = new GameServer(protocol, "1.0");

        final NettyServer server = new NettyServer("localhost", 6969, protocol, gameServer);
        server.bind();

        gameServer.getWorldManager().addWorld("TutorialWorld", new WorldAdapter(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.start();

        System.err.println("Server running.");

    }

}
