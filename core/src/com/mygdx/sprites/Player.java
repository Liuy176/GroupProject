package com.mygdx.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.EnemyGameScreen;
import com.mygdx.helpers.Constants;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite{
    public World world;
    public Body body;
    private TextureRegion stand;
    public enum State {FALLING, JUMPING, STANDING, RUNNING };
    public State currState;
    public State prevState;
    private Animation<TextureRegion> run;
    private Animation<TextureRegion> jump;
    private boolean facingRight;
    private float timer;
    private EnemyGameScreen screen;

    public Player(World world, EnemyGameScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;
        this.screen = screen;
        definePlayer();
        currState = State.STANDING;
        prevState = State.STANDING;
        timer = 0;
        facingRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i =1; i<4; i++)
            frames.add(new TextureRegion(getTexture(), i*16, 10, 16, 17));
        run = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for(int i=4; i<6; i++)
            frames.add(new TextureRegion(getTexture(), i*16, 10, 16, 17));
        jump = new Animation<TextureRegion>(0.2f, frames);
        

        stand = new TextureRegion(getTexture(), 0, 10, 16, 17);
        setBounds(0,0, 16/Constants.PPM, 16/Constants.PPM);
        setRegion(stand);
    }

    public void update(float delta){
        setPosition(body.getPosition().x-getWidth()/2, body.getPosition().y - getHeight()/2);
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta){
        currState = getState();

        TextureRegion region;
        switch (currState) {
            case JUMPING:
                region = jump.getKeyFrame(timer);
                break;
            case RUNNING:
                region = run.getKeyFrame(timer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region= stand;
                break;
        }
        if((body.getLinearVelocity().x<0 || !facingRight) && !region.isFlipX()){
            region.flip(true,false);
            facingRight = false;
        }
        else if((body.getLinearVelocity().x>0 || facingRight)&& region.isFlipX()){
            region.flip(true, false);
            facingRight = true;
        }
        timer = currState == prevState ? timer + delta : 0;
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

    public void definePlayer(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32/Constants.PPM,200/Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixture = new FixtureDef();
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(4/Constants.PPM,7/Constants.PPM);

        fixture.shape = playerShape;
        fixture.filter.categoryBits = Constants.CATEGORY_PLAYER;
        fixture.filter.maskBits = Constants.CATEGORY_GROUND;
        body.createFixture(fixture);
    }

    public void shoot() {
        // Determine the position and direction to shoot the projectile
        float x = body.getPosition().x + (facingRight ? 1 : -1) * 1;
        float y = body.getPosition().y;
    
        // Create a new projectile
        Bullet bullet = new Bullet(world, x, y, facingRight, 2);
        screen.addBullet(bullet);
        // Add the projectile to a list or directly to your game screen for rendering and updates
    }
    
}