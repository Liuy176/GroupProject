package com.mygdx.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.helpers.Constants;

public class Enemy extends Sprite {
    private World world;
    private Body body;
    private TextureRegion enemyTexture;
    private float speed;
    private Player player;
    private float startHealth;
    private float currentHealth;
    public Texture white;
    public TextureRegion whiteRegion;


    public Enemy(World world,float x, float y, float speed, float health, Player player) {
        
        this.world = world;
        this.speed = speed;
        this.player = player;
        this.startHealth = health;
        this.currentHealth = health;
        this.white = new Texture("white.png");
        this.whiteRegion = new TextureRegion(white, 0,0,1,1);

        defineEnemy(x, y);
    }

    public void update(float dt) {
        
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);


        Vector2 playerPos = player.body.getPosition();
        Vector2 enemyPos = body.getPosition();

        float direction = playerPos.x - enemyPos.x;
        direction = Math.signum(direction);

        float desiredVel = direction*speed;
        float velChange = desiredVel - body.getLinearVelocity().x;
        float impulse = body.getMass() * velChange;
        body.applyLinearImpulse(new Vector2(impulse,0), body.getWorldCenter(), true);
        //Vector2 direction = playerPos.sub(body.getPosition()).nor();
        //body.setLinearVelocity(direction.scl(speed));
    }

    private void defineEnemy(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / Constants.PPM, y / Constants.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

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
    }

    public void jump() {
        body.applyLinearImpulse(new Vector2(0, 3), body.getWorldCenter(), true);
    }

    public void takeDamage(float amount) {
        currentHealth -= amount;
        if (currentHealth < 0) currentHealth = 0;
    }

    public float getHealthPercentage() {
        return currentHealth / startHealth;
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

}
