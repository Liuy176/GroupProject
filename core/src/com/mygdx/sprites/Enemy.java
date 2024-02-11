package com.mygdx.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.EnemyGameScreen;
import com.mygdx.helpers.Constants;
import com.mygdx.helpers.Shaders;

public class Enemy extends Sprite {
    private World world;
    public Body body;
    private float speed, damage, startHealth, currentHealth, deadRotationDeg, timer;
    private Player player;
    public Texture white, armTexture;
    public TextureRegion whiteRegion, stand, jump;
    private boolean isDefeated, facingRight = false, movingBack = false;
    private EnemyGameScreen screen;
    private Animation<TextureRegion> run;
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD };
    private State currState, prevState;
    private Fixture fix;

    private float randomActionTimer = 0f;
    private float randomActionInterval = 3.0f;
    private float randomActionChance = 1.0f;
    private float moveBackDuration = 0f;
    private float moveBackTimer = 0f;
    private Vector2 moveBackDirection = new Vector2();

    private float timeSinceLastShot = 0f;
    private float shootingInterval = 1f;

    private Sprite armSprite;
    private int isDamaged = 10;
    private Shaders shader;

    public Enemy(World world,float x, float y, float speed, float health, Player player, EnemyGameScreen screen, float damage) {
        super(screen.getAtlas().findRegion("enemy"));
        this.world = world;
        this.speed = speed;
        this.damage = damage;
        this.player = player;
        this.screen = screen;
        this.startHealth = health;
        this.currentHealth = health;
        this.white = new Texture("white.png");
        this.whiteRegion = new TextureRegion(white, 0,0,1,1);
        this.isDefeated = false;
        this.deadRotationDeg = 0;
        this.shader = new Shaders();

        armTexture = new Texture("arm.png");
        armSprite = new Sprite(armTexture);
        armSprite.flip(true, true);
        armSprite.setSize((armTexture.getWidth()-3) / Constants.PPM, (armTexture.getHeight()-2) / Constants.PPM);
        armSprite.setOrigin(0, armSprite.getHeight() / 2);
        
        setOrigin(getWidth() / 2, getHeight() / 2);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        
        frames.add(new TextureRegion(getTexture(), 197, 4, 17, 27));
        frames.add(new TextureRegion(getTexture(), 214, 4, 17, 27));
        run = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        
        stand = new TextureRegion(getTexture(), 197, 4, 17, 27);
        jump = new TextureRegion(getTexture(), 231, 4, 19, 27);
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
        float distance = enemyPos.dst(playerPos);

        // edit arms position according to the direction enemy is facing
        if(!facingRight) armSprite.setPosition(body.getPosition().x  -0.35f+ armSprite.getWidth() / 2, body.getPosition().y+0.1f -armSprite.getHeight() / 2);
        else armSprite.setPosition(body.getPosition().x -0.2f + armSprite.getWidth() / 2, body.getPosition().y+0.1f -armSprite.getHeight() / 2);

        if(!isDefeated){
            Vector2 directionToPlayer = new Vector2(player.getBody().getPosition()).sub(body.getPosition()).nor();
            float angle = directionToPlayer.angleDeg();

            armSprite.setRotation(angle);
            updateMovement(distance, playerPos, dt);
        
           /*  if (distance > Constants.enemyShootingDistance) {
                // move towards the player
                approachPlayer(playerPos);
                if(body.getLinearVelocity().x == 0){
                    performRandomActionOrStandStill(dt);
                }
            } else {
                performRandomActionOrStandStill(dt);
                //body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (timeSinceLastShot >= shootingInterval) {
                    shoot(playerPos);
                    timeSinceLastShot = 0f; 
                }
            } */
        } else{
            deadRotationDeg +=5;
            //rotate(deadRotationDeg);
            body.setTransform(body.getPosition(), (float)Math.toRadians(deadRotationDeg));
            if(body.getPosition().y <= -1){
               body.setActive(false);
               this.setPosition(0, -5);
               body.setTransform(0, -1,0);
               body.setLinearVelocity(0,0);
               body.setAngularVelocity(0);
            }
        }
    }

    public TextureRegion getFrame(float delta){
        currState = getState();
        TextureRegion region;
        boolean isPlayerRight = player.getBody().getPosition().x > this.body.getPosition().x;
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

        if((!isPlayerRight || !facingRight) && region.isFlipX()){
            region.flip(true,false);
            armSprite.flip(false, true);
            facingRight = false;
        }
        else if((isPlayerRight || facingRight)&& !region.isFlipX()){
            region.flip(true, false);
            armSprite.flip(false, true);
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

    public void approachPlayer(Vector2 playerPosition) {

            float directionX = Math.signum(playerPosition.x - body.getPosition().x);
            body.setLinearVelocity(directionX * speed, body.getLinearVelocity().y);
    }


     private void performRandomActionOrStandStill(float dt) {
        randomActionTimer += dt;
        if (randomActionTimer >= randomActionInterval) {
            randomActionTimer = 0f;
            
            if (MathUtils.random() < randomActionChance) {
                // movement in random direction
                float randomDirection = MathUtils.randomBoolean() ? 1f : -1f;
                body.applyForceToCenter(new Vector2(randomDirection * 40f, 0), true);

            }
        }
    }

    private void updateMovement(float distance, Vector2 playerPos, float dt) {
        if (movingBack) {
            moveBackTimer += dt;
            if (moveBackTimer < moveBackDuration) {
                body.applyForceToCenter(moveBackDirection, true);
            } else {
                movingBack = false; // End of intentional movement
            }
        } else {
            if (distance > Constants.enemyShootingDistance) {
                // move towards the player
                approachPlayer(playerPos);
                if(body.getLinearVelocity().x == 0){
                    performRandomActionOrStandStill(dt);
                }
            } else {
                performRandomActionOrStandStill(dt);
                //body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (timeSinceLastShot >= shootingInterval) {
                    shoot(playerPos);
                    timeSinceLastShot = 0f; 
                }
            }
        }
    }

    

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
        fix = body.createFixture(fixtureDef);
        fix.setUserData("enemy");

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

    // movement methods to prevent enemies getting stuck when running into a wall
    public void moveBack() {
        float forceMagnitude = speed > 3 ? -4f : -3f;
        moveBackDirection.set(forceMagnitude, 0);
        moveBackDuration = 0.3f;
        moveBackTimer = 0f;
        movingBack = true;
    }
    
    public void moveForward() {
        float forceMagnitude = speed > 3 ? 4f : 3f;
        moveBackDirection.set(forceMagnitude, 0);
        moveBackDuration = 0.3f;
        moveBackTimer = 0f;
        movingBack = true;
    }

    public void takeDamage() {
        currentHealth -= player.getDamage();
        isDamaged=0;
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
        float armLen = armSprite.getWidth();

        // arm tip position
        float armAngle = (float)Math.toRadians(armSprite.getRotation());
        float armXOffset = armLen * MathUtils.cos(armAngle);
        float armYOffset = armLen * MathUtils.sin(armAngle);
    
        // arm tim pos (taking into account player's position)
        float bulletSpawnX = body.getPosition().x + (armXOffset*20f) / Constants.PPM;
        float bulletSpawnY = body.getPosition().y + armYOffset  / Constants.PPM + 0.1f;
    
        // spawn the bullet
        boolean facingRight = playerPosition.x > body.getPosition().x;
        EnemyBullet bullet = new EnemyBullet(world, bulletSpawnX, bulletSpawnY, facingRight, Constants.bulletSpeed, player, this);
        screen.addEnemyBullet(bullet);

    }

    public void draw(SpriteBatch batch) {
        float damageEffectIntensity;
        if(isDamaged<10){
            damageEffectIntensity = 0.5f;
            isDamaged++;
        }else {damageEffectIntensity = 0.0f;}

        batch.setShader(shader.getShaderProgram());
        shader.getShaderProgram().setUniformf("u_damageEffect", damageEffectIntensity);
        setOriginCenter();
        // Update the position of the sprite to match the body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        if(isDefeated)setRotation(deadRotationDeg);
        // Draw the sprite
        super.draw(batch); 
        if(!isDefeated)armSprite.draw(batch);
        batch.setShader(null);
    }

    public float getDamage(){
        return damage;
    }

    public void dispose() {
        armTexture.dispose();
        white.dispose();
        shader.dispose();
    }

}
