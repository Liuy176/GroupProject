package com.mygdx.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
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
    private TextureRegion enemyTexture;
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

    /*private float movementDuration = 2.0f;
    private float movementTimer = 0.0f;
    private float currentDirectionX = 0.0f;
*/
    private float timeSinceLastShot = 0f;
    private float shootingInterval = 1f;
    
  

    public Enemy(World world,float x, float y, float speed, float health, Player player, EnemyGameScreen screen) {
        
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


        defineEnemy(x, y);
    }

    public void update(float dt) {

        timeSinceLastShot += dt;
        
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);


        Vector2 playerPos = player.body.getPosition();
        Vector2 enemyPos = body.getPosition();

        float distance = enemyPos.dst(playerPos);
        //float direction = playerPos.x - enemyPos.x;
        //direction = Math.signum(direction);

        //float desiredVel = direction*speed;
        //float velChange = desiredVel - body.getLinearVelocity().x;
        //float impulse = body.getMass() * velChange;
        //body.applyLinearImpulse(new Vector2(impulse,0), body.getWorldCenter(), true);
        //Vector2 direction = playerPos.sub(body.getPosition()).nor();
        //body.setLinearVelocity(direction.scl(speed));

        if (distance > optimalDistance) {
            // Move towards the player
            approachPlayer(playerPos);
        } else {
            // Either stand still or perform random action
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            //performRandomActionOrStandStill();
            if (timeSinceLastShot >= shootingInterval) {
                shoot(playerPos);
                timeSinceLastShot = 0f; // Reset the timer after shooting
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
            // Continue moving in the current direction
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
            body.applyLinearImpulse(new Vector2(-20,0), body.getWorldCenter(), true);
        else
            body.applyLinearImpulse(new Vector2(-6,0), body.getWorldCenter(), true);
    }

    public void moveForward() {
        if(speed >3)
            body.applyLinearImpulse(new Vector2(20,0), body.getWorldCenter(), true);
        else
            body.applyLinearImpulse(new Vector2(6,0), body.getWorldCenter(), true);
    }

    public void takeDamage() {
        currentHealth -= player.damage;
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

}
