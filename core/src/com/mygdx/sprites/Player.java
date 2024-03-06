package com.mygdx.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.EnemyGameScreen;
import com.mygdx.game.SpaceBlastGame;
import com.mygdx.helpers.Constants;
import com.mygdx.helpers.Shaders;
import com.mygdx.helpers.SoundManager;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite{
    private World world;
    private SpaceBlastGame game;
    private Body body;
    private TextureRegion stand;
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD };
    private State currState, prevState;
    private Animation<TextureRegion> run;
    private TextureRegion jump;
    //private Animation<TextureRegion> idle;
    private boolean facingRight;
    private float timer;
    private EnemyGameScreen screen;
    private float startHealth, currentHealth;
    private Texture heartTexture, gunTexture, healthFrame, white, weaponBarFrame;
    private TextureRegion whiteRegion;
    private boolean isDefeated;
    private int jumpcounter = 2, isDamaged = 10;
    private float damage;
    private Shaders shader;
    private SoundManager sounds;

    public Player(SpaceBlastGame game, World world, EnemyGameScreen screen, float maxHealth, float weaponStrength, float currHealth, SoundManager sounds){
        super(screen.getAtlas().findRegion("player"));
        this.world = world;
        this.game = game;
        this.screen = screen;
        this.sounds = sounds;
        this.definePlayer();
        this.currState = State.STANDING;
        this.prevState = State.STANDING;
        this.timer = 0;
        this.facingRight = true;
        this.damage = weaponStrength;
        this.startHealth = maxHealth;
        this.currentHealth = currHealth;
        this.white = new Texture("white.png");
        this.whiteRegion = new TextureRegion(white, 0,0,1,1);
        this.heartTexture = new Texture("heart.png");
        this.gunTexture = new Texture("gun.png");
        this.healthFrame = new Texture("healthFrame.png");
        this.weaponBarFrame = new Texture("weaponBarFrame2.png");
        this.isDefeated = false;
        this.shader = new Shaders();

        setBounds(0,0, 16/Constants.PPM, 16/Constants.PPM);
        setRegion(stand);
    }

    public void update(float delta){
        // set position of the rexture
        if(facingRight) setPosition((body.getPosition().x-getWidth()/2) + 0.2f, body.getPosition().y - getHeight()/2);
        else setPosition((body.getPosition().x-getWidth()/2) - 0.2f, body.getPosition().y - getHeight()/2);
        
        setRegion(getFrame(delta));

        // fall through the ground when defeated
        if(isDefeated){
            if(body.getPosition().y <= -1){
               body.setActive(false);
               this.setPosition(0, -1);
               body.setTransform(0, -1,0);
               body.setLinearVelocity(0,0);
               body.setAngularVelocity(0);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        float damageEffectIntensity;

        // managing the duration of player's texture becoming red after being damaged
        if(isDamaged<10){
            damageEffectIntensity = 0.5f;
            isDamaged++;
        }else {damageEffectIntensity = 0.0f;}

        game.getBatch().setShader(shader.getShaderProgram());
        shader.getShaderProgram().setUniformf("u_damageEffect", damageEffectIntensity);
    
        super.draw(batch);
        game.getBatch().setShader(null);
    }

     // get appropriate texture for the player
    public TextureRegion getFrame(float delta){
        currState = getState();

        TextureRegion region;
        switch (currState) {
            case JUMPING:
                region = jump;
                break;
            case RUNNING:
                region = run.getKeyFrame(timer, true);
                break;
            case DEAD:
                region = stand;
                return region;
            case FALLING:
            case STANDING:
            default:
                region= stand;
                break;
        }
        // flip the texture according to player's direction of movement
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

    // determine player's state
    public State getState(){
        if(isDefeated)
            return State.DEAD;
        else if(body.getLinearVelocity().y>0 || (body.getLinearVelocity().y < 0 && prevState == State.JUMPING))
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
        bodyDef.position.set(32/Constants.PPM,340/Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setUserData(this);
 
        FixtureDef fixture = new FixtureDef();
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(4/Constants.PPM,7/Constants.PPM);

        fixture.shape = playerShape;
        // collision bits
        fixture.filter.categoryBits = Constants.CATEGORY_PLAYER;
        fixture.filter.maskBits = Constants.CATEGORY_GROUND | Constants.CATEGORY_ENEMY_BULLET;
        body.createFixture(fixture).setUserData(this);

        // create a sensor at the bottom of player's body
        EdgeShape leftSideClose = new EdgeShape();
        leftSideClose.set(new Vector2(-4/Constants.PPM, -8/Constants.PPM), new Vector2(4/Constants.PPM, -8/Constants.PPM));
        fixture.shape = leftSideClose;
        fixture.isSensor = true;
        body.createFixture(fixture).setUserData("jumpSensor");

        // create running animation
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(getTexture(), 116, 0, 26, 31));
        frames.add(new TextureRegion(getTexture(), 142, 0, 26, 31));
        run = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        
        stand = new TextureRegion(getTexture(), 116, 0, 26, 31);
        jump = new TextureRegion(getTexture(),168, 0, 29, 31);
    }

    public void shoot() {
        
        float x = body.getPosition().x + (facingRight ? 1 : -1) * 0.7f;
        float y = body.getPosition().y+0.2f;
    
        Bullet bullet = new Bullet(world, x, y, facingRight, Constants.bulletSpeed);
        screen.addBullet(bullet);
        sounds.playShotSound();
    }

    public float getHealthPercentage() {
        return currentHealth / startHealth;
    }

    // draws health bar and weapon power bar in the top right corner of the screen
    public void drawHealthBar(SpriteBatch batch, BitmapFont font) {
        float healthPercentage = currentHealth/Constants.maxPlayerHealth;
        float weaponStrengthPercentage = (float)damage/Constants.maxWeaponPower;

        float barWidth = Constants.healthBarWidth;
        float barHeight = Constants.healthBarHeight;
        float padding = Constants.healthBarPadding;
        float heartSize = 32;

        float barX = Gdx.graphics.getWidth() - barWidth - padding;
        float barY = Gdx.graphics.getHeight() - barHeight - padding;
        float weaponBarY = barY - (barHeight + padding+5);
        float heartX = barX - heartSize - 20;
        float heartY = barY + (barHeight - heartSize) / 2;

        batch.draw(heartTexture, heartX, heartY, 40, 32);
        batch.draw(gunTexture, heartX, weaponBarY, 40, 32);

        //background of healthbar
        batch.setColor(Color.RED);
        batch.draw(whiteRegion, barX, barY, barWidth, barHeight);

        //foreground of healthbar
        batch.setColor(Color.GREEN);
        batch.draw(whiteRegion, barX, barY, barWidth * healthPercentage, barHeight);

        batch.setColor(Color.GRAY); // background of weapon power bar
        batch.draw(whiteRegion, barX, weaponBarY, barWidth, barHeight);

        batch.setColor(Color.YELLOW); // foreground of weapon ower bar
        batch.draw(whiteRegion, barX, weaponBarY, barWidth * weaponStrengthPercentage, barHeight);

        batch.setColor(Color.WHITE);
        batch.draw(healthFrame, barX-3, barY-3, barWidth*1.03f, barHeight*1.25f);
        batch.draw(weaponBarFrame, barX-3, weaponBarY-3,barWidth*1.03f, barHeight*1.25f);
    }

    public void takeDamage(float amount) {
        currentHealth -= amount;
        isDamaged = 0;
        if (currentHealth <= 0) {
            currentHealth = 0;
            playerDies();
        }
        sounds.playEnemyHit();
    }
    
    private void playerDies() {
        isDefeated = true;
        for (Fixture fixture : body.getFixtureList()) { // make player not collide with anything once it gets defeated
            Filter filter = fixture.getFilterData();
            filter.maskBits = 0;
            fixture.setFilterData(filter);
        }

        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
        screen.setDefeated(true);
    }

    public void jump(){
        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        body.applyLinearImpulse(new Vector2(0,5.5f), body.getWorldCenter(), true);
        jumpcounter++;  // for double jump
    }

    public void dispose(){
        heartTexture.dispose();
        gunTexture.dispose();
        white.dispose();
        healthFrame.dispose();
        sounds.dispose();
        shader.dispose();
    }

    public World getWorld(){
        return world;
    }
    public Body getBody(){
        return body;
    }
    public float getCurrentHealth(){
        return currentHealth;
    }
    public boolean getIsDefeated(){
        return isDefeated;
    }
    public float getDamage(){
        return damage;
    }
    public int getJumpCounter(){
        return jumpcounter;
    }
    public void setJumpCounter(int count){
        this.jumpcounter = count;
    }
}