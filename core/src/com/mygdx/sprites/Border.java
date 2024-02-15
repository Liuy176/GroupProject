package com.mygdx.sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.helpers.Constants;

public class Border extends InteractiveObject{

    public final Fixture fix;
    
    // Creates border around the map
    public Border(World world, TiledMap map, Rectangle bounds){

        super(world, map, bounds);
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixture = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.getX()+bounds.getWidth()/2)/Constants.PPM, (bounds.getY()+bounds.getHeight()/2)/Constants.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox(bounds.getWidth()/2/Constants.PPM, bounds.getHeight()/2/Constants.PPM);
        fixture.shape = shape;

        // set collision bits
        fixture.filter.categoryBits = Constants.CATEGORY_GROUND;
        fixture.filter.maskBits = Constants.CATEGORY_PLAYER | Constants.CATEGORY_ENEMY | Constants.CATEGORY_BULLET | Constants.CATEGORY_ENEMY_BULLET;

        fix = body.createFixture(fixture);
    }
}
