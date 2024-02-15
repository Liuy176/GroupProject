package com.mygdx.helpers;


public final class Constants {

    // pixels per meter
    public static final float PPM = 16.0f;

    public static final short CATEGORY_PLAYER = 0x0001; // 0001 in binary
    public static final short CATEGORY_ENEMY = 0x0002;  // 0010 in binary
    public static final short CATEGORY_GROUND = 0x0004; // etc
    public static final short CATEGORY_BULLET = 0x0008;
    public static final short CATEGORY_ENEMY_BULLET = 0x0010;

    // edit values below to change difficulty and edit other aspects of gameplay

    // SpaceshipScreen elements

    // distance between the 2 asteroids where player needs to fly through
    private static final float asteroidDistanceEasy = 340;
    private static final float asteroidDistanceMedium = 295;
    private static final float asteroidDistanceHard = 265;

    // distance based on difficulty
    public static float getAsteroidBatchDistance(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return asteroidDistanceEasy;
            case "Hard":
                return asteroidDistanceHard;
            case "Medium":
            default:
                return asteroidDistanceMedium;
        }
    }

    public static final int xPosOfUfoAtStart = 200;
    public static final float frequencyOfHealthPowerUp = 0.4f; // how often power up shows up
    public static final float frequencyOfWeaponPowerUp = 0.3f; // how often power up shows up
    public static final int healthPowerUpMovementSpeed = 200;
    public static final int weaponPowerUpMovementSpeed = 250;
    public static final int healthPowerUpValue = 50; // how much health does player regain after claiming the power up
    public static final int weaponPowerUpValue = 6; // how much extra weapon power does player get after claiming the power up

    // time interval between spawning asteroid batches
    private static final float asteroidIntervalEasy = 1.4f;
    private static final float asteroidIntervalMid = 1.2f;
    private static final float asteroidIntervalHard = 0.88f;

    // time intervals accoirding to difficulty
    public static float getAsteroidInterval(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return asteroidIntervalEasy;
            case "Hard":
                return asteroidIntervalHard;
            case "Medium":
            default:
                return asteroidIntervalMid;
        }
    }
    
    // asteroid movement speed
    private static final int asteroidSpeedEasy = 350;
    private static final int asteroidSpeedMid = 420;
    private static final int asteroidSpeedHard = 600;

    // asteroid movement speed according to difficulty
    public static float getAsteroidSpeed(String difficulty) {
        switch (difficulty) {
            case "Easy":
                return asteroidSpeedEasy;
            case "Hard":
                return asteroidSpeedHard;
            case "Medium":
            default:
                return asteroidSpeedMid;
        }
    }

    //EnemyGameScreen elements
    public static final int bulletDistanceLimit = 20; // fhow far bullet can travel before despawning
    public static final float enemyShootingDistance = 4f; // from how close does enemy start shooting at player
    public static final int bulletSpeed = 10;
    public static final int maxPlayerHealth = 200;
    public static final int maxWeaponPower = 120;
    public static final int initialWeaponPower = 12;


    // properties of health and weapon bars in the top right corner
    public static final int healthBarWidth = 200;
    public static final int healthBarHeight = 24;
    public static final int healthBarPadding = 15;

}
