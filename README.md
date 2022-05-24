# Lunar

#### What is Lunar?

Lunar is a networking library for LibGDX. With lunar you can easily create multiplayer games quickly and efficiently. Lunar provides many common utilities such as entities, worlds, protocol, and box2d support!

**Lunar is still in very early development, expect many, many changes.**

#### What's Coming
Lunar has been majorly overhauled since last release in October. None of the documentation reflects the new API.

I will soon be updating this repo with all current information and more examples once a few more issues are ironed out.

Until then you can;
- a) Use the old release from October, all current documentation will show you how to use Lunar.
- b) Go in blind and use current repo and try to build something. The code does have comments and documentation spread around.
- c) Wait.


#### Features
- Networked box2d worlds.. (WIP)
- Networked player moving and velocity.
- Networked player creation/removing.
- Ashley entity system
- A expandable protocol with SSL support.
- Very customizable and extendable.

### Get A Taste
```java
// apply force to another player and send it to others since they were attacked.
this.player.getWorldIn().applyForceToOtherPlayerNetwork(somePlayer, player.getConnection(), fx, fy, px, py, true);

// apply a knock-back force to ourselves.
this.player.getWorldIn().applyForceToPlayerNetwork(player.getConnection(), fx, fy, point.x, point.y, true);
```

```java
// register a unique custom packet.
this.connection.registerPacket(99, MyCustomPacket::new, packet -> handleEntityPropertiesPacket(packet));
```

```java
// Create a networked world for others to join us.
// We tell the world to handle physics updates and local player updates for us.
lunarWorld = new BasicLunarWorld(player, world, scaling, true, true, true);
// Spawn our player in the world.
player.spawnEntityInWorld(lunarWorld, 2.0f, 2.0f);
```

```java
// connect to remote server.
final LunarClientServer server = new LunarClientServer(lunar, "localhost", 6969);
server.connect().join();

// get our connection
final PlayerConnection connection = (PlayerConnection) server.getConnection();
```

Want to jump in? Check out the [Building A Simple Game](https://github.com/Vrekt/LunarGdx/wiki/Getting-Started-Guide:-Building-a-simple-game)

# Documentation and Examples

[Examples](https://github.com/Vrekt/LunarGdx/tree/main/core/src/gdx/examples)

[Wiki](https://github.com/Vrekt/LunarGdx/wiki)

# Planned Features
- Networked collision
- Better movement sync.
- Better protocol security.
- Networked entities and other map/world objects.
- ...

# Known Issues
- Random ghost player blinks next to networked player
- Initial position seems to be de-synced some way
- Worlds will need to be disposed when switching between them
- probably other stuff I don't know about.

# Using Lunar
You can find releases in the releases section. Both client and server rely on the Protocol dependency.

You must also add a dependency for netty-all.
##### Gradle 6.7.2
```java
implementation group: 'io.netty', name: 'netty-all', version: '4.1.48.Final'
```
