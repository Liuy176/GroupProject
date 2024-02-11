package com.mygdx.helpers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.sprites.Bullet;
import com.mygdx.sprites.Enemy;
import com.mygdx.sprites.EnemyBullet;
import com.mygdx.sprites.Ground;
import com.mygdx.sprites.Player;

public class WorldContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // make enemies jump if they bump into ground
        if (fixA.getUserData() instanceof Enemy || fixB.getUserData() instanceof Enemy) {
            Fixture enemyFixture = (fixA.getUserData() instanceof Enemy) ? fixA : fixB;
            Fixture otherFixture = enemyFixture == fixA ? fixB : fixA;

            if ("ground".equals(otherFixture.getUserData())) {
                Enemy enemy = (Enemy) enemyFixture.getUserData();
                if(enemy.body.getLinearVelocity().y == 0)
                    enemy.jump();
            }
        }

        // enemy takes damage if hit by a bullet
        if (fixA.getUserData() instanceof Bullet || fixB.getUserData() instanceof Bullet) {
            Fixture bulletFixture = (fixA.getUserData() instanceof Bullet) ? fixA : fixB;
            Fixture other = bulletFixture == fixA ? fixB: fixA;
            Bullet bullet = (Bullet) bulletFixture.getUserData();
            bullet.setToRemove(true);

            if(other.getUserData() instanceof Enemy){
                Enemy enemy = (Enemy) other.getUserData();
                enemy.takeDamage();
            } /*else if (other.getUserData() !=null &&( other.getUserData().equals("enemyBackupLeft") || other.getUserData().equals("enemyBackupRight"))){
                Enemy enemy = (Enemy) other.getBody().getUserData();
                enemy.takeDamage();
            }*/
        }

        // player takes damage if hit by enemy's bullet
        if (fixA.getUserData() instanceof EnemyBullet || fixB.getUserData() instanceof EnemyBullet) {
            Fixture bulletFixture = (fixA.getUserData() instanceof EnemyBullet) ? fixA : fixB;
            Fixture other = bulletFixture == fixA ? fixB: fixA;
            EnemyBullet bullet = (EnemyBullet) bulletFixture.getUserData();
            bullet.setToRemove(true);;
  
            if(other.getUserData() instanceof Player){
                Player player = (Player) other.getUserData();
                player.takeDamage(bullet.getEnemy().getDamage());
            }
        }

        // push the enemy in the opposite direction from the wall if it gets stuck there and can't jump
        if(fixA.getUserData() != null && fixB.getUserData() != null && (fixA.getUserData().equals("enemyBackupLeft")|| fixB.getUserData().equals("enemyBackupLeft"))){
            Fixture enemyFix = (fixA.getUserData().equals("enemyBackupLeft")) ? fixA : fixB;
            Enemy enemy = (Enemy) enemyFix.getBody().getUserData();
            enemy.moveForward();
        }

        // push the enemy in the opposite direction from the wall if it gets stuck there and can't jump
        if(fixA.getUserData() != null && fixB.getUserData() != null && (fixA.getUserData().equals("enemyBackupRight")|| fixB.getUserData().equals("enemyBackupRight"))){
            Fixture enemyFix = (fixA.getUserData().equals("enemyBackupRight")) ? fixA : fixB;
            Enemy enemy = (Enemy) enemyFix.getBody().getUserData();
            enemy.moveBack();
        }

        if(fixA.getUserData() != null && fixB.getUserData() != null && (fixA.getUserData().equals("jumpSensor")|| fixB.getUserData().equals("jumpSensor"))){
            Fixture fix = (fixA.getUserData().equals("jumpSensor")) ? fixA : fixB;
            Fixture other = fix == fixA ? fixB: fixA;
            Player player = (Player) fix.getBody().getUserData();

            if ("ground".equals(other.getUserData())) player.setJumpCounter(0);
        }

    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
    
}
