package com.mygdx.objects.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.helpers.Constants;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;


public class Player extends GameEntity{

    private int jumpCounter;
    
    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = 8f;
        this.jumpCounter = 0;
    }

    public void update(){
        x = body.getPosition().x * Constants.PPM;
        y = body.getPosition().y * Constants.PPM;

        checkUserInput();
    }

    public void render(SpriteBatch batch){

    }

    private void checkUserInput(){
        velX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) velX = 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) velX = -1;

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCounter<2) {
            float force = body.getMass() *18;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCounter++;
        }

        if(body.getLinearVelocity().y ==0){
            jumpCounter = 0;
        }

        body.setLinearVelocity(velX*speed, body.getLinearVelocity().y <25 ? body.getLinearVelocity().y : 25);
    }
}
