package lunar.shared.contact;

import com.badlogic.gdx.physics.box2d.*;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * A basic collision listener for {@link lunar.shared.entity.player.LunarEntityPlayer}
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
        final Fixture a = contact.getFixtureA();
        final Fixture b = contact.getFixtureB();

        if (a.getUserData() instanceof LunarEntityPlayer
                && b.getUserData() instanceof LunarEntityPlayer) {
            final LunarEntityPlayer playerA = (LunarEntityPlayer) a.getUserData();
            // TODO: Possibly check for playerB as-well
            if (playerA.isPlayerCollisionDisabled()) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
