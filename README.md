# Lunar

#### What is Lunar?

Lunar is a networking library for LibGDX. With lunar you can easily create multiplayer games quickly and efficiently. Lunar provides many common utilities such as entities, worlds, protocol, and box2d support!

#### Features
- Player and entity renderers with animation loading.
- Networked Box2d worlds.
- Networked player moving and velocity.
- Networked player creation/removing.
- A very basic protocol with SSL encryption.
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
- Lobbies
- ...
