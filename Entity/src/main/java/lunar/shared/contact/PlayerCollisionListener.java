package lunar.shared.contact;

import com.badlogic.gdx.physics.box2d.*;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * A basic collision listener for {@link LunarEntityPlayer}
 */
public final class PlayerCollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        // disable collisions between players\
        if (fixtureA.getUserData() instanceof LunarEntityPlayer
                && fixtureB.getUserData() instanceof LunarEntityPlayer) {
            final LunarEntityPlayer a = (LunarEntityPlayer) fixtureA.getUserData();
            final LunarEntityPlayer b = (LunarEntityPlayer) fixtureB.getUserData();
            if (a.doIgnorePlayerCollision() || b.doIgnorePlayerCollision()) contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
