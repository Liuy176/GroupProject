package com.mygdx.helpers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TileMap {
    
    private TiledMap tiledMap;

    public TileMap(){

    }

    public OrthogonalTiledMapRenderer  setupMap() {
        tiledMap = new TmxMapLoader().load("../../../../../assets/map1.tmx");
        return new OrthogonalTiledMapRenderer(tiledMap);
    }
}
