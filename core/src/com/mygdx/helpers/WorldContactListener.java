package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.sprites.Enemy;
import com.mygdx.sprites.Ground;
import com.mygdx.sprites.InteractiveObject;

public class WorldContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'beginContact'");
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Check if either of the fixtures is one of the enemy's sides
        if (fixA.getUserData() instanceof Enemy || fixB.getUserData() instanceof Enemy) {
            Fixture enemyFixture = (fixA.getUserData() instanceof Enemy) ? fixA : fixB;
            Fixture otherFixture = enemyFixture == fixA ? fixB : fixA;

        // Check if the other fixture is the ground
        if ("ground".equals(otherFixture.getUserData())) {
            Enemy enemy = (Enemy) enemyFixture.getUserData();
            enemy.jump();
        }
    }
    }

    @Override
    public void endContact(Contact contact) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'endContact'");
        Gdx.app.log("End", "");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'preSolve'");
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'postSolve'");
    }
    
}
