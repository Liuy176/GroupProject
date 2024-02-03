package com.mygdx.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.EnemyGameScreen;
import com.mygdx.helpers.Constants;

public class Enemy extends Sprite {
    private World world;
    public Body body;
    private float speed;
    private Player player;
    private float startHealth;
    private float currentHealth;
    public Texture white;
    public TextureRegion whiteRegion;
    private boolean isDefeated;
    private float deadRotationDeg;
    private EnemyGameScreen screen;
    private float optimalDistance = 3.0f;
    private TextureRegion stand;
    private Animation<TextureRegion> run;
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD };
    private State currState, prevState;
    private float timer;

    /*private float movementDuration = 2.0f;
    private float movementTimer = 0.0f;
    private float currentDirectionX = 0.0f;
*/
    private float timeSinceLastShot = 0f;
    private float shootingInterval = 1f;

    private Texture texture;
    private TextureRegion enemyTextureRegion;
    private Sprite armSprite;
    private Texture armTexture;
    private boolean facingRight = false;
  

    public Enemy(World world,float x, float y, float speed, float health, Player player, EnemyGameScreen screen) {
        super(screen.getAtlas().findRegion("enemy"));
        this.world = world;
        this.speed = speed;
        this.player = player;
        this.screen = screen;
        this.startHealth = health;
        this.currentHealth = health;
        this.white = new Texture("white.png");
        this.whiteRegion = new TextureRegion(white, 0,0,1,1);
        this.isDefeated = false;
        this.deadRotationDeg = 0;

        armTexture = new Texture("arm.png"); // Assuming you have an arm texture
        armSprite = new Sprite(armTexture);
        armSprite.flip(true, true);
        armSprite.setSize(armTexture.getWidth() / Constants.PPM, armTexture.getHeight() / Constants.PPM);
        armSprite.setOrigin(0, armSprite.getHeight() / 2);
        
        setOrigin(getWidth() / 2, getHeight() / 2);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        
        frames.add(new TextureRegion(getTexture(), 0, 0, 17, 27));
        frames.add(new TextureRegion(getTexture(), 17, 0, 17, 27));
        run = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        
        stand = new TextureRegion(getTexture(), 0, 0, 17, 27);
        setBounds(0,0, 13/Constants.PPM, 16/Constants.PPM);
        setRegion(stand);
        defineEnemy(x, y);
    }

    public void update(float dt) {

        timeSinceLastShot += dt;
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));


        Vector2 playerPos = player.getBody().getPosition();
        Vector2 enemyPos = body.getPosition();
        //dVector2 directionToPlayer = player.getBody().getPosition().sub(body.getPosition());
        //Vector2 directionToPlayer = playerPos.sub(body.getPosition()).nor();
        Vector2 directionToPlayer = new Vector2(player.getBody().getPosition()).sub(body.getPosition()).nor();
        float angle = directionToPlayer.angleDeg();
        float distance = enemyPos.dst(playerPos);
        //float direction = playerPos.x - enemyPos.x;
        //direction = Math.signum(direction);

        //float desiredVel = direction*speed;
        //float velChange = desiredVel - body.getLinearVelocity().x;
        //float impulse = body.getMass() * velChange;
        //body.applyLinearImpulse(new Vector2(impulse,0), body.getWorldCenter(), true);
        //Vector2 direction = playerPos.sub(body.getPosition()).nor();
        //body.setLinearVelocity(direction.scl(speed));

        armSprite.setRotation(angle);
        armSprite.setPosition(body.getPosition().x - 0.2f + armSprite.getWidth() / 2, body.getPosition().y +0.1f-armSprite.getHeight() / 2);
        
        if (distance > optimalDistance) {
            // move towards the player
            approachPlayer(playerPos);
        } else {
            
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            
            if (timeSinceLastShot >= shootingInterval) {
                shoot(playerPos);
                timeSinceLastShot = 0f; 
            }
        }

        if(isDefeated){
            deadRotationDeg +=5;
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
        boolean isPlayerRight = player.getBody().getPosition().x > this.body.getPosition().x;
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

        if (!facingRight && isPlayerRight && !region.isFlipX()) {
            region.flip(true, false);
            armSprite.flip(false, true);
            facingRight = true; //
        } 
        
        else if (facingRight && !isPlayerRight && region.isFlipX()) {
            region.flip(true, false);
            armSprite.flip(false, true);
            facingRight = false;
        }

           
        timer = currState == prevState ? timer + delta : 0;
        prevState = currState;
        return region;
    }

    public State getState(){
        if(isDefeated)
            return State.DEAD;
        //else if(body.getLinearVelocity().y>0 || (body.getLinearVelocity().y < 0 && prevState == State.JUMPING))
        //    return State.JUMPING;
        else if(body.getLinearVelocity().y<0)
            return State.FALLING;
        else if(body.getLinearVelocity().x !=0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void approachPlayer(Vector2 playerPosition) {
  
        float directionX = Math.signum(playerPosition.x - body.getPosition().x);
        body.setLinearVelocity(directionX * speed, body.getLinearVelocity().y);
    }


    /*public void randomMovement() {
        if (movementTimer <= 0) {
            //choose a new direction
            currentDirectionX = MathUtils.random(-1f, 1f);
            movementTimer = movementDuration; // Reset the timer
        } else {
            // continue moving in the current direction
            body.setLinearVelocity(currentDirectionX * speed, body.getLinearVelocity().y);
            movementTimer -= Gdx.graphics.getDeltaTime(); // decrease the timer
        }
    }

    private void performRandomActionOrStandStill() {
        if (MathUtils.randomBoolean()) {
            speed = 3; 
        } else {
            speed = 0;
        }
        randomMovement();
    }
    */

    private void defineEnemy(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4 / Constants.PPM, 7 / Constants.PPM);

        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = Constants.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits = Constants.CATEGORY_GROUND | Constants.CATEGORY_BULLET;
        body.createFixture(fixtureDef);

        EdgeShape rightSide = new EdgeShape();
        rightSide.set(new Vector2(7/Constants.PPM, 6/Constants.PPM), new Vector2(7/Constants.PPM, -6/Constants.PPM));
        fixtureDef.shape = rightSide;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape leftSide = new EdgeShape();
        leftSide.set(new Vector2(-7/Constants.PPM, 6/Constants.PPM), new Vector2(-7/Constants.PPM, -6/Constants.PPM));
        fixtureDef.shape = leftSide;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
        

        EdgeShape leftSideClose = new EdgeShape();
        leftSideClose.set(new Vector2(-4/Constants.PPM, 6/Constants.PPM), new Vector2(-4/Constants.PPM, -6/Constants.PPM));
        fixtureDef.shape = leftSideClose;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("enemyBackupLeft");

        EdgeShape rightSideClose = new EdgeShape();
        rightSideClose.set(new Vector2(4/Constants.PPM, 6/Constants.PPM), new Vector2(4/Constants.PPM, -6/Constants.PPM));
        fixtureDef.shape = rightSideClose;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("enemyBackupRight");
    }

    public void jump() {
        body.applyLinearImpulse(new Vector2(0, 3), body.getWorldCenter(), true);
    }

    public void moveBack() {
        if(speed >3)
            body.applyLinearImpulse(new Vector2(-30,0), body.getWorldCenter(), true);
        else
            body.applyLinearImpulse(new Vector2(-6,0), body.getWorldCenter(), true);
    }

    public void moveForward() {
        if(speed >3)
            body.applyLinearImpulse(new Vector2(30,0), body.getWorldCenter(), true);
        else
            body.applyLinearImpulse(new Vector2(6,0), body.getWorldCenter(), true);
    }

    public void takeDamage() {
        currentHealth -= player.getDamage();
        if (currentHealth <= 0) {
            currentHealth = 0;
            enemyDies();
        }
    }

    public float getHealthPercentage() {
        return currentHealth / startHealth;
    }

    private void enemyDies() {
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.maskBits = 0; // no collision
            fixture.setFilterData(filter);
        }

        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
        isDefeated = true;
        screen.enemyCount--;
    }

    public void drawHealthBar(SpriteBatch batch) {
        float healthPercentage = getHealthPercentage();
        float barWidth = 12/Constants.PPM;
        float barHeight = 1/Constants.PPM;
        float padding = 11/Constants.PPM;

        float barX = this.getX() + (this.getWidth() - barWidth) / 2;
        float barY = this.getY() + this.getHeight() + padding;

        //background
        batch.setColor(Color.RED);
        batch.draw(whiteRegion, barX, barY, barWidth, barHeight);

        //foreground of the bar
        batch.setColor(Color.GREEN);
        batch.draw(whiteRegion, barX, barY, barWidth * healthPercentage, barHeight);

        batch.setColor(Color.WHITE);
    }  
    
    public void shoot(Vector2 playerPosition) {
        float x = body.getPosition().x;
        boolean facingRight = playerPosition.x > x;
        float y = body.getPosition().y;

        x = x + (facingRight ? 1 : -1) * 0.7f;
        
        EnemyBullet bullet = new EnemyBullet(world, x, y, facingRight, 12, player);
        screen.addEnemyBullet(bullet);

    }

    public void draw(SpriteBatch batch) {
        // Update the position of the sprite to match the body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        
        // Draw the sprite
        super.draw(batch);
        armSprite.draw(batch);
    }
    public void dispose() {
        texture.dispose();
        armTexture.dispose();
    }

}
