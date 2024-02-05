package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.helpers.Constants;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class SpaceshipScreen implements Screen {
    private SpriteBatch batch;
    private Texture img, tNave, tEnemy1, tCandy, tWeapon, weaponBarFrame;
    private Sprite nave;
    private float posX, posY;
    private boolean  gameover;
    private Array<Rectangle> enemies1;

    private int score, damage=10;
    private boolean toIncrementScore = false;
  
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;
    private Array<Rectangle> candies, weapons;
    private long lastCandyTime, lastWeaponTime;
    
    private boolean paused; 
    private MyGdxGame game;
    
    private float gravity = 0.5f; 
    private float jumpVelocity = -10f; 
    private float currentVelocity = 0f;

    private boolean isBlinking;
    private float blinkStartTime;
    private float blinkDuration = 2f;
    private float blinkInterval = 0.2f; 
    private boolean isShipVisible = true;

    private float lastAsteroidBatchX = 0;
    private float timeSinceLastAsteroidPair = 0f;
    private float pairGenInterval = 1f;
    private boolean fadeOut = false;
    private float fadeOutSpeed = 0.5f;
    private float fadeOutOpacity = 0.0f;
    private float collisionTimer = 0f;
    private int timesCrashed;
    public float playerHealth;

    private EnemyGameScreen disposeEnemyScreen = null;
    private Texture heartTexture, healthFrame, white, gunTexture;
    private TextureRegion whiteRegion;
    private BitmapFont font;

  public SpaceshipScreen(MyGdxGame game, float health){
    this.game = game;
    batch = game.getBatch();
    img = new Texture("9.png");
    tNave = new Texture("ship-1.png.png");
    nave = new Sprite(tNave);
    posX = Constants.xPosOfUfoAtStart;
    posY = 0;

    tCandy = new Texture("healthPickup.png");
    tWeapon = new Texture("gunPickup.png");
    candies = new Array<Rectangle>();
    weapons = new Array<Rectangle>();
    lastCandyTime = TimeUtils.nanoTime();
    lastWeaponTime = TimeUtils.nanoTime();

    tEnemy1 = new Texture("asteroidNew.png");
    enemies1 = new Array<Rectangle>();

    playerHealth = health;
    timesCrashed = 0;
    score = 0;

    generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
    parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    
    parameter.size = 30;
    parameter.borderWidth = 1;
    parameter.borderColor = Color.BLACK;
    parameter.color = Color.WHITE;
    bitmap = generator.generateFont(parameter);

    gameover = false;
    isBlinking=false;
    blinkStartTime = 0f;
    paused=false; 
    
    this.white = new Texture("white.png");
    this.whiteRegion = new TextureRegion(white, 0,0,1,1);
    this.heartTexture = new Texture("heart.png");
    this.gunTexture = new Texture("gun.png");
    this.healthFrame = new Texture("healthFrame.png");
    this.weaponBarFrame = new Texture("weaponBarFrame.png");

  }
  
    @Override
    public void render(float delta) {
      if(!isBlinking) // can't pause/unpause shortly before swithcing to another screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) paused = !paused;

      if(!paused){
        this.moveNave();
        this.moveEnemies(delta);
        this.moveCandy();
      }

      if(disposeEnemyScreen!=null) {
        disposeEnemyScreen.dispose();
        disposeEnemyScreen = null;
      }

      if (isBlinking) {
        float elapsed = (TimeUtils.nanoTime() - blinkStartTime) *0.000000001f;
        if (elapsed > blinkDuration) {
            isBlinking = false;
            paused = false;
            isShipVisible = true;
        } else {
            if ((int)(elapsed / blinkInterval) % 2 == 0) {
                isShipVisible = true;
            }else {
              isShipVisible = false;
            }
        }
    } 
    
    if (fadeOut) {
      fadeOutOpacity += fadeOutSpeed * delta;
      fadeOutOpacity = Math.min(fadeOutOpacity, 1.0f); // opacity< 1
      } 
  
      ScreenUtils.clear(1, 0, 0, 1);
      batch.begin();
      batch.draw(img, 0, 0);
      drawHealthBar(game.getBatch(), font);
     //if (paused) bitmap.draw...
      if(!gameover){
        
        if(isShipVisible) batch.draw(nave, posX, posY, nave.getWidth()*4, nave.getHeight()*4 );
        for (Rectangle candy : candies) {
          batch.draw(tCandy, candy.x, candy.y, candy.width*3, candy.height*3);
        }
        for (Rectangle weapon : weapons) {
          batch.draw(tWeapon, weapon.x, weapon.y, weapon.width*3, weapon.height*3);
        }
  
        for(Rectangle enemy : enemies1 ){
          batch.draw(tEnemy1, enemy.x, enemy.y, enemy.width * 2, enemy.height*2);
        }
         
        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);

      }else{
        
        bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
      }
      batch.end();

      if (fadeOut) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        batch.setColor(0, 0, 0, fadeOutOpacity);
        batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1); // Reset color
        batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
     }
    }
  
    @Override
    public void dispose () {
      //batch.dispose();
      //img.dispose();
      //tNave.dispose();
      //tCandy.dispose();
      //white.dispose();
      //healthFrame.dispose();
      //heartTexture.dispose();
      //gunTexture.dispose();
      //weaponBarFrame.dispose();
    }
  
    private void moveNave(){
      if(paused) return;

      if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
          restart(true);
      }
      if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
          currentVelocity = jumpVelocity; // jump
      }

      // gravity
      currentVelocity += gravity;
      posY -= currentVelocity;

      if(posY < 0){
          posY = 0;
      }
      if(posY > Gdx.graphics.getHeight() - nave.getHeight()){
          posY = Gdx.graphics.getHeight() - nave.getHeight();
      }
    }
  
    private void produceCandy(Texture texture, Array<Rectangle> items) {
      Rectangle item = new Rectangle(
              Gdx.graphics.getWidth(),
              MathUtils.random(0, Gdx.graphics.getHeight() - texture.getHeight()),
              texture.getWidth(),
              texture.getHeight()
      );
      items.add(item);
    }
  
    private void moveCandy() {
      if(paused) return; 
    
      if ((TimeUtils.nanoTime() - lastCandyTime) * Constants.frequencyOfHealthPowerUp > 2000000000) { // Adjust time as needed
        this.produceCandy(tCandy, candies);
        lastCandyTime = TimeUtils.nanoTime();
      }
      if ((TimeUtils.nanoTime() - lastWeaponTime) * Constants.frequencyOfWeaponPowerUp > 2000000000) { // Adjust time as needed
        this.produceCandy(tWeapon, weapons);
        lastWeaponTime = TimeUtils.nanoTime();
      }
  
      for (Iterator<Rectangle> iter = candies.iterator(); iter.hasNext(); ) {
        Rectangle candy = iter.next();
        candy.x -= Constants.healthPowerUpMovementSpeed * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
        if (candy.x + tCandy.getWidth() < 0) iter.remove();
        else if (collide(candy.x, candy.y, candy.width*3, candy.height*3, posX, posY, nave.getWidth()*4, nave.getHeight()*4)) {
          // Restore power
          if(playerHealth>=100)
            playerHealth=100;
          else
            playerHealth += Constants.healthPowerUpValue;
          iter.remove();
        }
      }

      for (Iterator<Rectangle> iter = weapons.iterator(); iter.hasNext(); ) {
        Rectangle weapon = iter.next();
        weapon.x -= Constants.weaponPowerUpMovementSpeed * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
        if (weapon.x + tWeapon.getWidth() < 0) iter.remove();
        else if (collide(weapon.x, weapon.y, weapon.width*3, weapon.height*3, posX, posY, nave.getWidth()*4, nave.getHeight()*4)) {
          if(damage>=100)
            damage=100;
          else
            damage += Constants.weaponPowerUpValue;
          iter.remove();
        }
      }
    }
  
    private void moveEnemies(float delta) {
      if(paused) return; 

      this.produceAsteroidPair(delta);
  
      for (Iterator<Rectangle> iter = enemies1.iterator(); iter.hasNext();) {
        Rectangle enemy = iter.next();
        enemy.x -= Constants.asteroidMovementSpeed * Gdx.graphics.getDeltaTime();
        
        if (enemy.x < lastAsteroidBatchX) {
          lastAsteroidBatchX = enemy.x;
        }
        // Check if the player has moved past an asteroid batch, and increase the score accordingly
        if (enemy.x + enemy.width < 0) {
          iter.remove();
        }
        if(enemy.x < posX && enemy.x + Constants.asteroidMovementSpeed * Gdx.graphics.getDeltaTime() >= Constants.xPosOfUfoAtStart){
          if(!toIncrementScore){
            toIncrementScore =true;
          }
          else {
            score++;
            toIncrementScore = false;
          }
        }
  
        // Check for collision with the ship
        if (collide(enemy.x, enemy.y, enemy.width*2, enemy.height*2, posX, posY, nave.getWidth()*4, nave.getHeight()*4)) {
          if (!gameover) {
            isBlinking = true;
            blinkStartTime = TimeUtils.nanoTime();
            paused = true; 
            timesCrashed++;
            fadeOut = true;
            collisionTimer = 0;
          }
          iter.remove();
        }

        if(fadeOut){
          collisionTimer +=delta;
          if(collisionTimer>=1){
            game.setScreen(new EnemyGameScreen(game, timesCrashed, 100, damage, playerHealth));
          }
        }
      }
    }
  
  
    private boolean collide ( float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2){
          // Shrink the collision area slightly for more precise detection
          float shrinkFactor = 0.8f;
          float newW1 = w1 * shrinkFactor;
          float newH1 = h1 * shrinkFactor;
          float newW2 = w2 * shrinkFactor;
          float newH2 = h2 * shrinkFactor;
  
          // Adjust positions to keep the reduced area centered
          float newX1 = x1 + (w1 - newW1) / 2;
          float newY1 = y1 + (h1 - newH1) / 2;
          float newX2 = x2 + (w2 - newW2) / 2;
          float newY2 = y2 + (h2 - newH2) / 2;
  
          // Check collision with adjusted areas
          if (newX1 + newW1 > newX2 && newX1 < newX2 + newW2 && newY1 + newH1 > newY2 && newY1 < newY2 + newH2) {
            return true;
          }
          return false;
        }

        private void produceAsteroidPair(float delta) {
          timeSinceLastAsteroidPair += delta;
            if (timeSinceLastAsteroidPair > pairGenInterval) {
                float baseY = MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy1.getHeight() * 2 - Constants.asteroidBatchDistance);
                float y1 = baseY;
                float y2 = baseY + tEnemy1.getHeight() + Constants.asteroidBatchDistance;
        
                createAsteroid(Gdx.graphics.getWidth(), y1);
                createAsteroid(Gdx.graphics.getWidth(), y2);
        
                timeSinceLastAsteroidPair = 0f;
          }
      
      }

      private void createAsteroid(float x, float y) {
          Rectangle asteroid = new Rectangle(x, y, tEnemy1.getWidth(), tEnemy1.getHeight());
          enemies1.add(asteroid);
      }
      
      public void restart(boolean isPaused) {
        //score = 0;
        //power = 3;
        if(!isPaused) score = 0;
        posX = Constants.xPosOfUfoAtStart;
        enemies1.clear();
        candies.clear();
        lastCandyTime = TimeUtils.nanoTime();
        lastWeaponTime = TimeUtils.nanoTime();
        gameover = false;
        paused = isPaused;
        isBlinking = false;
        fadeOutOpacity=0f;
        fadeOut=false;
    }

    public void drawHealthBar(SpriteBatch batch, BitmapFont font) {
      float healthPercentage = playerHealth/100;
      float weaponStrengthPercentage = (float)damage/100;
      float barWidth = 200;
      float barHeight = 24;
      float padding = 15;
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
      batch.draw(healthFrame, barX-3, barY-3,206, 30);
      batch.draw(weaponBarFrame, barX-3, weaponBarY-3,206, 30);
  }

    public void setDisposeEnemyScreen(EnemyGameScreen screen){
      this.disposeEnemyScreen = screen;
    }
    public void setPlayerHealth(float health){
      this.playerHealth = health;
    }
    public void setDamage(int strength){
      this.damage = strength;
    }
    public void setAmountOfCrashes(int num){
      this.timesCrashed = num;
    }
    public void setScore(int score){
      this.score = score;
    }
    public void setGameOver(boolean isGameOver){
      this.gameover = isGameOver;
    }
    public void setFadeOut(boolean isFadeOut){
      this.fadeOut = isFadeOut;
    }
    public void clearEnemies(){
      enemies1.clear();
    }
    public void clearCandies(){
      candies.clear();
    }
    public void setFadeOpacity(float opacity){
      this.fadeOutOpacity=opacity;
    }
    public void setIsBlinking(boolean blinking){
      this.isBlinking = blinking;
    }
    public int getScore(){
      return score;
    }
    @Override
    public void show() {
      font = new BitmapFont();
      font.setColor(Color.WHITE);
      font.getData().setScale(1);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {} 
  
}
