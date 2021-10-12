package me.vrekt.testiong;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.LunarGameServer;
import gdx.lunar.server.LunarNettyServer;

public class Main {

    public static void main(String[] args) {

        final LunarProtocol protocol = new LunarProtocol(true);
        final LunarNettyServer server = new LunarNettyServer("localhost", 6969, protocol);
        server.bind();

        final LunarGameServer gameServer = new LunarGameServer();
        gameServer.start();

    }

}
