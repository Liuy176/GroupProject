package com.mygdx.helpers;


public final class Constants {

    public static final float PPM = 16.0f;

    public static final short CATEGORY_PLAYER = 0x0001; // 0001 in binary
    public static final short CATEGORY_ENEMY = 0x0002;  // 0010 in binary
    public static final short CATEGORY_GROUND = 0x0004; // etc
    public static final short CATEGORY_BULLET = 0x0008;
    public static final short CATEGORY_ENEMY_BULLET = 0x0010;

    // edit values below to change difficulty and edit other aspects of gameplay

    // SpaceshipScreen elements
    public static final float asteroidBatchDistance = 210; // distance between the 2 asteroids where player needs to fly through
    public static final int xPosOfUfoAtStart = 200;
    public static final float frequencyOfHealthPowerUp = 0.3f; // how often power up shows up
    public static final float frequencyOfWeaponPowerUp = 0.5f; // how often power up shows up
    public static final int healthPowerUpMovementSpeed = 200;
    public static final int weaponPowerUpMovementSpeed = 250;
    public static final int healthPowerUpValue = 50; // how much health does player regain after claiming the power up
    public static final int weaponPowerUpValue = 10; // how much extra weapon power does player get after claiming the power up
    public static final int asteroidMovementSpeed = 400;

    //EnemyGameScreen elements
    public static final int bulletDistanceLimit = 25; // fhow far bullet can travel before despawning
    public static final float enemyShootingDistance = 3.5f; // from how close does enemy start shooting at player
    public static final int bulletSpeed = 10;

}
