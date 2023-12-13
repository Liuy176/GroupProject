package com.mygdx.objects.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.helpers.Constants;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends GameEntity{
    
    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = 4f;

    }

    public void update(){
        x = body.getPosition().x * Constants.PPM;
        y = body.getPosition().y * Constants.PPM;
    }

    public void render(SpriteBatch batch){

    }
}
