package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class TiledGameMap {

    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    MapLayers mapLayers;


    public TiledGameMap() {
        tiledMap = new TmxMapLoader().load("background/Map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        mapLayers = tiledMap.getLayers();
    }

    public void render (OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        mapLayers.get("Sun").setOffsetX(camera.position.x - 900);
    }

    public void dispose() {
        tiledMap.dispose();
    }

}
