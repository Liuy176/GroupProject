package com.mygdx.objects.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.EnemyGameScreen;
import com.mygdx.helpers.Constants;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Sprite{

    private int jumpCounter;
    public float x, y, velX, velY, speed;
    public float width, height;
    public Body body;
    private TextureRegion stand;
    private TextureAtlas atlas;
    public enum State {FALLING, JUMPING, STANDING, RUNNING };
    public State currState;
    public State prevState;
    private Animation<TextureRegion> run;
    private Animation<TextureRegion> jump;
    private boolean runnungRight;
    private float stateTimer;
    
    public Player(float width, float height, Body body, EnemyGameScreen screen) {
        super(new TextureAtlas("Mario_and_Enemies.pack").findRegion("little_mario"));
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.width = width;
        this.height = height;
        this.velX = 0;
        this.velY = 0;
        this.speed = 0;
        this.body = body;
        this.speed = 2f;
        this.jumpCounter = 0;
        stand = new TextureRegion(getTexture(),0,12,16,16);

        currState = State.STANDING;
        prevState = State.STANDING;
        stateTimer = 0;
        runnungRight = true;
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i<4; i++)
            frames.add(new TextureRegion(getTexture(), i*16,12,16,16));
        run = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for(int i = 4; i<6; i++)
            frames.add(new TextureRegion(getTexture(), i*16,12,16,16));
        jump = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        setBounds(0,0, 16/Constants.PPM, 16/Constants.PPM);
        setRegion(stand);
    }

    public void update(){
        x = body.getPosition().x * Constants.PPM;
        y = body.getPosition().y * Constants.PPM;
        setPosition(body.getPosition().x-(9/32f), body.getPosition().y-(7/32f));
        setRegion(getFrame(5));

        checkUserInput();
    }

    public TextureRegion getFrame(float dt){
        currState = getState();

        TextureRegion region;
        switch (currState) {
            case JUMPING:
                region = jump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = run.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region= stand;
                break;
        }
        if((body.getLinearVelocity().x<0 || !runnungRight) && !region.isFlipX()){
            region.flip(true,false);
            runnungRight = false;
        }
        else if((body.getLinearVelocity().x>0 || runnungRight)&& region.isFlipX()){
            region.flip(true, false);
            runnungRight = true;
        }
        stateTimer = currState == prevState ? stateTimer + dt : 0;
        prevState = currState;
        return region;
    }

    public State getState(){
        if(body.getLinearVelocity().y>0 || (body.getLinearVelocity().y < 0 && prevState == State.JUMPING))
            return State.JUMPING;
        else if(body.getLinearVelocity().y<0)
            return State.FALLING;
        else if(body.getLinearVelocity().x !=0)
            return State.RUNNING;
        else
            return State.STANDING;
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
            body.applyLinearImpulse(new Vector2(0, 6), body.getPosition(), true);
            jumpCounter++;
        }

        if(body.getLinearVelocity().y ==0){
            jumpCounter = 0;
        }

        body.setLinearVelocity(velX*speed, body.getLinearVelocity().y <25 ? body.getLinearVelocity().y : 25);
    }

    public Body getBody() {
        return body;
    }
}
