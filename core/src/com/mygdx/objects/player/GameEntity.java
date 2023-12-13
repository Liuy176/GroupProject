package com.mygdx.objects.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameEntity {

    public float x, y, velX, velY, speed;
    public float width, height;
    public Body body;

    public GameEntity(float widht, float height, Body body){
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.width = widht;
        this.height = height;
        this.velX = 0;
        this.velY = 0;
        this.speed = 0;
        this.body = body;
    }

    public abstract void update();

    public abstract void render(SpriteBatch batch);

    public Body getBody() {
        return body;
    }
    
}
