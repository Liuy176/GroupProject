package com.mygdx.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
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

    public Bullet(World world, float x, float y, boolean facingRight, float speed) {
        this.world = world;
        this.speed = speed;
        this.facingRight = facingRight;

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
        body.createFixture(fixtureDef);
        
        body.setLinearVelocity((facingRight ? 1 : -1) * speed, 0); // Set velocity based on direction
    }

    public void update(float dt) {
        // Update the projectile's position to match the Box2D body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    public Body getBody(){
        return body;
    }
}

