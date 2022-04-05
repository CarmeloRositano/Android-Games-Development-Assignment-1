package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class TiledGameMap extends Widget {

    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    MapLayers mapLayers;
    float sunOffset;
    float groundOffset;
    float foregroundOneOffset;
    float foregroundTwoOffset;
    float backgroundOffset;



    public TiledGameMap() {
        tiledMap = new TmxMapLoader().load("background/Map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        mapLayers = tiledMap.getLayers();

        sunOffset = 0f;
        groundOffset = 0f;
        foregroundOneOffset = 0f;
        foregroundTwoOffset = 0f;
        backgroundOffset = 0f;
    }

    public void render (OrthographicCamera camera) {

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        mapLayers.get("Sun").setOffsetX((camera.position.x - camera.viewportWidth) - sunOffset);
        mapLayers.get("Foreground 1").setOffsetX((camera.position.x - camera.viewportWidth) - foregroundOneOffset);
        mapLayers.get("Foreground 2").setOffsetX((camera.position.x - camera.viewportWidth) - foregroundTwoOffset);
        mapLayers.get("Ground").setOffsetX((camera.position.x - camera.viewportWidth) - groundOffset);
        mapLayers.get("Background").setOffsetX((camera.position.x - camera.viewportWidth) - backgroundOffset);

        //Increment Offset
        sunOffset+=0.01f;
        backgroundOffset +=0.1f;
        foregroundOneOffset+=Player.getConstantSpeed() * 0.01f;
        foregroundTwoOffset+=Player.getConstantSpeed() * 0.001f;
        groundOffset+=Player.getConstantSpeed() * 0.05f;

        //Reset Offset
        if (sunOffset >= 1920) sunOffset = 0;
        if (foregroundOneOffset >= 1920) foregroundOneOffset = 0;
        if (foregroundTwoOffset >= 1920) foregroundTwoOffset = 0;
        if (groundOffset >= 1920) groundOffset = 0;
        if (backgroundOffset >= 1920) backgroundOffset = 0;
    }

    public void dispose() {
        tiledMap.dispose();
    }

}
