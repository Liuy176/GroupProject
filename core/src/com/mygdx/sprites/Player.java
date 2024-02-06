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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.EnemyGameScreen;
import com.mygdx.helpers.Constants;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite{
    private World world;
    private Body body;
    private TextureRegion stand;
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD };
    private State currState, prevState;
    private Animation<TextureRegion> run;
    private Animation<TextureRegion> jump;
    private boolean facingRight;
    private float timer;
    private EnemyGameScreen screen;
    private float startHealth, currentHealth;
    private Texture heartTexture, gunTexture, healthFrame, white, weaponBarFrame;
    private TextureRegion whiteRegion;
    private boolean isDefeated;
    private int deadRotationDeg, jumpcounter = 0;
    private float damage;

    public Player(World world, EnemyGameScreen screen, float maxHealth, float weaponStrength, float currHealth){
        super(screen.getAtlas().findRegion("player"));
        this.world = world;
        this.screen = screen;
        definePlayer();
        currState = State.STANDING;
        prevState = State.STANDING;
        timer = 0;
        facingRight = true;
        this.damage = weaponStrength;
        this.startHealth = maxHealth;
        this.currentHealth = currHealth;
        this.white = new Texture("white.png");
        this.whiteRegion = new TextureRegion(white, 0,0,1,1);
        this.heartTexture = new Texture("heart.png");
        this.gunTexture = new Texture("gun.png");
        this.healthFrame = new Texture("healthFrame.png");
        this.weaponBarFrame = new Texture("weaponBarFrame.png");
        this.isDefeated = false;
        this.deadRotationDeg = 0;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        
        frames.add(new TextureRegion(getTexture(), 0, 29, 26, 31));
        frames.add(new TextureRegion(getTexture(), 26, 29, 26, 31));
        run = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        
        stand = new TextureRegion(getTexture(), 0, 29, 26, 31);
        setBounds(0,0, 16/Constants.PPM, 16/Constants.PPM);
        setRegion(stand);
    }

    public void update(float delta){
        if(facingRight) setPosition((body.getPosition().x-getWidth()/2) + 0.2f, body.getPosition().y - getHeight()/2);
        else setPosition((body.getPosition().x-getWidth()/2) - 0.2f, body.getPosition().y - getHeight()/2);
        
        setRegion(getFrame(delta));
        if(body.getLinearVelocity().y ==0) jumpcounter = 0;

        if(isDefeated){
            deadRotationDeg +=0;
            rotate(deadRotationDeg);
            body.setTransform(body.getPosition(), (float)Math.toRadians(deadRotationDeg));
            if(body.getPosition().y <= -1){
               body.setActive(false);
               this.setPosition(0, -1);
               body.setTransform(0, -1,0);
               body.setLinearVelocity(0,0);
               body.setAngularVelocity(0);
            }
        }
    }

    public TextureRegion getFrame(float delta){
        currState = getState();

        TextureRegion region;
        switch (currState) {
            case JUMPING:
                region = stand;
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
        bodyDef.position.set(32/Constants.PPM,200/Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
 
        FixtureDef fixture = new FixtureDef();
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(4/Constants.PPM,7/Constants.PPM);

        fixture.shape = playerShape;
        fixture.filter.categoryBits = Constants.CATEGORY_PLAYER;
        fixture.filter.maskBits = Constants.CATEGORY_GROUND | Constants.CATEGORY_ENEMY_BULLET;
        body.createFixture(fixture).setUserData(this);;
    }

    public void shoot() {
        
        float x = body.getPosition().x + (facingRight ? 1 : -1) * 0.7f;
        float y = body.getPosition().y+0.2f;
    
        Bullet bullet = new Bullet(world, x, y, facingRight, Constants.bulletSpeed);
        screen.addBullet(bullet);
    }

    public float getHealthPercentage() {
        return currentHealth / startHealth;
    }

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

        //String healthText = "PLAYER'S HP: ";
        //font.draw(batch, healthText, barX - 100, barY + barHeight);
        batch.draw(heartTexture, heartX, heartY, 40, 32);
        batch.draw(gunTexture, heartX, weaponBarY, 40, 32);

        //background
        batch.setColor(Color.RED);
        batch.draw(whiteRegion, barX, barY, barWidth, barHeight);

        //foreground
        batch.setColor(Color.GREEN);
        batch.draw(whiteRegion, barX, barY, barWidth * healthPercentage, barHeight);

        batch.setColor(Color.GRAY); // background
        batch.draw(whiteRegion, barX, weaponBarY, barWidth, barHeight);

        batch.setColor(Color.YELLOW); // foreground
        batch.draw(whiteRegion, barX, weaponBarY, barWidth * weaponStrengthPercentage, barHeight);

        batch.setColor(Color.WHITE);
        batch.draw(healthFrame, barX-3, barY-3, barWidth*1.03f, barHeight*1.25f);
        batch.draw(weaponBarFrame, barX-3, weaponBarY-3,barWidth*1.03f, barHeight*1.25f);
    }

    public void takeDamage(float amount) {
        currentHealth -= amount;
        if (currentHealth <= 0) {
            currentHealth = 0;
            playerDies();
        }
    }
    
    private void playerDies() {
        isDefeated = true;
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.maskBits = 0; // no collision
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
}