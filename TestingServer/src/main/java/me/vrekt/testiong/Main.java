package me.vrekt.testiong;

import gdx.lunar.server.LunarGameServer;
import gdx.lunar.server.LunarNettyServer;

public class Main {

    public static void main(String[] args) {
        final LunarNettyServer server = new LunarNettyServer("localhost", 6969);
        server.bind().join();

        final LunarGameServer s = new LunarGameServer();
        s.start();

    }

}
