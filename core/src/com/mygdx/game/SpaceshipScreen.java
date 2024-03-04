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
import com.mygdx.helpers.SoundManager;

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

    private int score, damage=Constants.initialWeaponPower, scoreWhenCrashed;
    private boolean toIncrementScore = false;
  
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;
    private Array<Rectangle> candies, weapons;
    private long lastCandyTime, lastWeaponTime, lastAsteroidPairTime;
    
    private boolean paused;
    private long gameTime = 0;
    private long pauseStartTime = 0;
    private long totalPauseTime = 0;
    private SpaceBlastGame game;
    private SoundManager sounds;
    
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
    private boolean fadeOut = false, firstCrash = true, collided = false;
    private float fadeOutSpeed = 0.5f;
    private float fadeOutOpacity = 0.0f;
    private float collisionTimer = 0f;
    private int timesCrashed;
    public float playerHealth;

    private EnemyGameScreen disposeEnemyScreen = null;
    private Texture heartTexture, healthFrame, white, gunTexture;
    private TextureRegion whiteRegion;
    private BitmapFont font;
    
    private String crashText = "Crashed... \n\n\nDefeat the enemies inhabiting the asteroid \n\nto continue your journey!";
    private StringBuilder currentText = new StringBuilder();
    private float charTimer = 0, charInterval = 0.04f;
    private int charIndex = 0;

  public SpaceshipScreen(SpaceBlastGame game, float health, SoundManager sounds){
    this.game = game;
    this.sounds = sounds;
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
    lastCandyTime = 0;
    lastWeaponTime = 0;
    lastAsteroidPairTime = 0;

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
    
    white = new Texture("white.png");
    whiteRegion = new TextureRegion(white, 0,0,1,1);
    heartTexture = new Texture("heart.png");
    gunTexture = new Texture("gun.png");
    healthFrame = new Texture("healthFrame.png");
    weaponBarFrame = new Texture("weaponBarFrame2.png");
  }
  
    @Override
    public void render(float delta) {
      if(!isBlinking && !(collisionTimer>0)) {// can't pause/unpause or do perform other actions shortly before swithcing to another screen
        handleInput();
      }

      // update actions on the screen accordingly
      if(!paused){
        this.moveNave();
        this.moveEnemies(delta);
        this.moveCandy();
        long currTime = TimeUtils.nanoTime();
        gameTime = currTime - totalPauseTime; // gameTime used for not taking into account time that is being spent in paused state of the game
      } 

      if (isBlinking) blinkShip(); //blinking turns on for a few seconds if the spaceship crashes
    
      if (fadeOut) {
      fadeOutOpacity += fadeOutSpeed * delta;
      fadeOutOpacity = Math.min(fadeOutOpacity, 1.0f); // opacity< 1
      } 
  
      ScreenUtils.clear(1, 0, 0, 1);
      batch.begin();
      batch.draw(img, 0, 0);
        
      if(isShipVisible) batch.draw(nave, posX, posY, nave.getWidth()*4, nave.getHeight()*4 );

      drawFlyingObjects(); // draw asteroids and claimable items
         
      bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20); // score label in top left corner

      drawHealthBar(game.getBatch(), font); // top right corner

      if(paused && !isBlinking) { // instructions appear when the game is paused (can't pause right when the spaceship crashed (it starts blinking then))
        game.getMenu().getFont().draw(batch, "Press SPACE to continue...", (Gdx.graphics.getWidth()/2)-215, (Gdx.graphics.getHeight()/2)+20);
        game.getMenu().getFont().draw(game.getBatch(), "(Press R to return to main menu)", (Gdx.graphics.getWidth()/2)-265, (Gdx.graphics.getHeight()/2)-20);
      }
      batch.end();

      if (fadeOut) fadeOut(delta);
    }
  
    @Override
    public void dispose () { // not implemented because the screen is used throughout the whole game. We change its state but never dispose
    }
  
    private void moveNave(){
      if(paused) return;

      if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
          currentVelocity = jumpVelocity; // jump
      }

      // gravity
      currentVelocity += gravity;
      posY -= currentVelocity;

      // keep the ship within the screen boundries
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
      
      // spawn claimable items
      if ((gameTime - lastCandyTime) * Constants.frequencyOfHealthPowerUp > 2000000000) { // Adjust time as needed
        this.produceCandy(tCandy, candies);
        lastCandyTime = gameTime;
      }
      if ((gameTime - lastWeaponTime) * Constants.frequencyOfWeaponPowerUp > 2000000000) { // Adjust time as needed
        this.produceCandy(tWeapon, weapons);
        lastWeaponTime = gameTime;
      }
  
      for (Iterator<Rectangle> iter = candies.iterator(); iter.hasNext(); ) {
        Rectangle candy = iter.next();
        candy.x -= Constants.healthPowerUpMovementSpeed * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
        if (candy.x + tCandy.getWidth() < 0) iter.remove();
        else if (collide(candy.x, candy.y, candy.width*3, candy.height*3, posX, posY, nave.getWidth()*4, nave.getHeight()*4)) {
          // Restore power
          if(playerHealth + Constants.healthPowerUpValue>=Constants.maxPlayerHealth)
            playerHealth=Constants.maxPlayerHealth;
          else
            playerHealth += Constants.healthPowerUpValue; // add health points after claiming the item
          iter.remove();
        }
      }

      for (Iterator<Rectangle> iter = weapons.iterator(); iter.hasNext(); ) {
        Rectangle weapon = iter.next();
        weapon.x -= Constants.weaponPowerUpMovementSpeed * Gdx.graphics.getDeltaTime(); // Adjust speed as needed
        if (weapon.x + tWeapon.getWidth() < 0) iter.remove();
        else if (collide(weapon.x, weapon.y, weapon.width*3, weapon.height*3, posX, posY, nave.getWidth()*4, nave.getHeight()*4)) {
          if(damage + Constants.weaponPowerUpValue>=Constants.maxWeaponPower) 
            damage=Constants.maxWeaponPower;
          else
            damage += Constants.weaponPowerUpValue; // add weapon power points after claiming the item
          iter.remove();
        }
      }
    }
  
    private void moveEnemies(float delta) {
      if(paused) return; 

      this.produceAsteroidPair();
  
      for (Iterator<Rectangle> iter = enemies1.iterator(); iter.hasNext();) {
        Rectangle enemy = iter.next();
        enemy.x -= Constants.getAsteroidSpeed(game.getDif()) * Gdx.graphics.getDeltaTime();
        
        if (enemy.x < lastAsteroidBatchX) {
          lastAsteroidBatchX = enemy.x;
        }
        // Check if the player has moved past an asteroid batch, and increase the score accordingly
        if (enemy.x + enemy.width < 0) {
          iter.remove();
        }
        if(enemy.x < posX && enemy.x + Constants.getAsteroidSpeed(game.getDif()) * Gdx.graphics.getDeltaTime() >= Constants.xPosOfUfoAtStart){
          if(!toIncrementScore){
            toIncrementScore =true;
          }
          else {
            score++;
            toIncrementScore = false;
          }
        }
  
        // Check for collision with the ship
        if (collide(enemy.x, enemy.y, enemy.width*3, enemy.height*3, posX, posY, nave.getWidth()*4, nave.getHeight()*4) && !collided) {
          if (!gameover) {
            collided = true;
            isBlinking = true;
            blinkStartTime = TimeUtils.nanoTime();
            paused = true; 
            timesCrashed++;
            fadeOut = true;
            collisionTimer = 0;
            sounds.playExplosion();
          }
          iter.remove();
        }

        if(fadeOut) fadeOutAndSwitch(delta);
      }
    }
    
    private void drawFlyingObjects(){
      for (Rectangle candy : candies) {
        batch.draw(tCandy, candy.x, candy.y, candy.width*3, candy.height*3);
      }
      for (Rectangle weapon : weapons) {
        batch.draw(tWeapon, weapon.x, weapon.y, weapon.width*3, weapon.height*3);
      }
      for(Rectangle enemy : enemies1 ){
        batch.draw(tEnemy1, enemy.x, enemy.y, enemy.width * 3, enemy.height*3);
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

    private void produceAsteroidPair() {
          timeSinceLastAsteroidPair = (gameTime - lastAsteroidPairTime) / 1000000000.0f;
          if (timeSinceLastAsteroidPair > Constants.getAsteroidInterval(game.getDif())) {
              float baseY = MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy1.getHeight()*2 - Constants.getAsteroidBatchDistance(game.getDif()));
              float y1 = baseY;
              float y2 = baseY + tEnemy1.getHeight() + Constants.getAsteroidBatchDistance(game.getDif());
        
              createAsteroid(Gdx.graphics.getWidth(), y1);
              createAsteroid(Gdx.graphics.getWidth(), y2);
            
              lastAsteroidPairTime = gameTime;
              //timeSinceLastAsteroidPair = 0f;
         }
    }

    private void createAsteroid(float x, float y) {
        Rectangle asteroid = new Rectangle(x, y, tEnemy1.getWidth(), tEnemy1.getHeight());
        enemies1.add(asteroid);
    }
    
    // reset the game mode presented in this screen
    public void restart(boolean isPaused) {
      posX = Constants.xPosOfUfoAtStart;
      enemies1.clear();
      candies.clear();
      gameover = false;
      paused = isPaused;
      isBlinking = false;
      fadeOutOpacity=0f;
      fadeOut=false;
      collisionTimer = 0;
      gameTime = 0;
      pauseStartTime = 0;
      totalPauseTime = 0;
      timeSinceLastAsteroidPair=0;
      lastAsteroidBatchX = 0;
      lastCandyTime = 0;
      lastWeaponTime = 0;
      lastAsteroidPairTime = 0;
      if(!isPaused) {
        score = 0;
        playerHealth = Constants.maxPlayerHealth;
        damage = Constants.initialWeaponPower;
        timesCrashed = 0;
        lastAsteroidPairTime = TimeUtils.nanoTime();
        lastCandyTime = TimeUtils.nanoTime();
        lastWeaponTime = TimeUtils.nanoTime();
      }
    }

    // draws health bar and weapon power bar in the top right corner of the screen
    public void drawHealthBar(SpriteBatch batch, BitmapFont font) {
      float healthPercentage = playerHealth/Constants.maxPlayerHealth;
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

    // transition effect when changing screens
    private void fadeOut(float delta){
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      batch.begin();
      batch.setColor(0, 0, 0, fadeOutOpacity);
      batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      if(fadeOutOpacity>=1 && firstCrash){
        updateText(delta);
        game.getMenu().getFont().draw(batch, currentText.toString(), 100, Gdx.graphics.getHeight() / 3);
      }
      batch.setColor(1, 1, 1, 1); // reset color
      batch.end();
      Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // transition to the EnemyGameScreen game mode
    private void fadeOutAndSwitch(float delta){
      collisionTimer +=delta;
      if((collisionTimer>=1 && !firstCrash) || collisionTimer>=31){
        collided = false;
        collisionTimer = 0;
        firstCrash = false;
        sounds.getBackground1().pause();
        game.setScreen(new EnemyGameScreen(game, timesCrashed, Constants.maxPlayerHealth, damage, playerHealth, sounds));
      }
    }

    // display text instructions during the transition (when crashed for the first time)
    public void updateText(float delta) {
      if (charIndex < crashText.length()) {
          charTimer += delta;
          if (charTimer >= charInterval) {
              currentText.append(crashText.charAt(charIndex++));
              charTimer = 0;
          }
      }
    }

    //blinking animation of the spaceship texture
    private void blinkShip(){
      float elapsed = (TimeUtils.nanoTime() - blinkStartTime) *0.000000001f;
      if (elapsed > blinkDuration) {
          scoreWhenCrashed = score;
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

    private void handleInput(){
      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        if(paused){
          paused = false;
          totalPauseTime += TimeUtils.nanoTime() - pauseStartTime; // make time not account for the time when the game is paused
        } else {
          paused = true;
          pauseStartTime = TimeUtils.nanoTime(); // make time not account for the time when the game is paused
        }
      }
      if(paused && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){ 
        paused = false;
      }
      if(paused && Gdx.input.isKeyJustPressed(Input.Keys.R)){ // return to menu
        sounds.getBackground1().pause();
        game.setScreen(game.getMenu());
        restart(false);
      }
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
    public int getScoreWhenCrashed(){
      return scoreWhenCrashed;
    }
    public BitmapFont getBitmap(){
      return bitmap;
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
    public void hide() {
      // dispose the enemyGmaeScreen we switched from to this screen(if that screen exists)
      if(disposeEnemyScreen!=null) {
        disposeEnemyScreen.dispose();
        disposeEnemyScreen = null;
      }
    } 
  
}
