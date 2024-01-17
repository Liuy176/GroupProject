package com.mygdx.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.helpers.Constants;

public class Bullet extends Sprite {
    private World world;
    private Body body;
    private float speed;
    private boolean facingRight;

    private Vector2 startPosition;
    private float distanceLimit;
    private float distanceTraveled;
    public boolean toRemove;

    public Bullet(World world, float x, float y, boolean facingRight, float speed) {
        this.world = world;
        this.speed = speed;
        this.facingRight = facingRight;
        this.startPosition = new Vector2(x, y);
        this.distanceLimit = 20;
        this.distanceTraveled = 0;
        this.toRemove = false;

        defineBullet(x, y);
    }

    private void defineBullet(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x , y );
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.gravityScale = 0; // Projectiles typically aren't affected by gravity
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2 / Constants.PPM, 2 / Constants.PPM); // Adjust size as needed

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.CATEGORY_BULLET;
        fixtureDef.filter.maskBits = Constants.CATEGORY_GROUND | Constants.CATEGORY_ENEMY;
        
        body.createFixture(fixtureDef).setUserData(this);
        
        body.setLinearVelocity((facingRight ? 1 : -1) * speed, 0); // Set velocity based on direction
    }

    public void update(float dt) {
        // Update the projectile's position to match the Box2D body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        distanceTraveled = startPosition.dst(body.getPosition().x, body.getPosition().y);
        if (distanceTraveled > distanceLimit) {
            // Handle the bullet removal, e.g., set a flag or directly remove from the game
            this.toRemove = true; // Assuming there's a 'remove' flag in the Bullet class
        }
    }

    public Body getBody(){
        return body;
    }
}

