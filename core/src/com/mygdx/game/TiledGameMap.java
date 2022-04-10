package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
    float timeElapsed;

    public TiledGameMap() {
        tiledMap = new TmxMapLoader().load("background/Map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        mapLayers = tiledMap.getLayers();

        sunOffset = 0f;
        groundOffset = 0f;
        foregroundOneOffset = 0f;
        foregroundTwoOffset = 0f;
        backgroundOffset = 0f;
        timeElapsed = 0f;
    }

    public void render (OrthographicCamera camera, MyGdxGame.GameState gameState, Player player) {


        //Speed up the player movement over time
        timeElapsed += Gdx.graphics.getDeltaTime();
        if(timeElapsed >= 1000f) timeElapsed = 1000f;
        if(gameState == MyGdxGame.GameState.MAIN_MENU) timeElapsed = 0f; //Stop game from speeding up if in menu

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        //Parallax Effect
        mapLayers.get("Sun").setOffsetX((camera.position.x - camera.viewportWidth) - sunOffset);
        mapLayers.get("Foreground 1").setOffsetX((camera.position.x - camera.viewportWidth) - foregroundOneOffset);
        mapLayers.get("Foreground 2").setOffsetX((camera.position.x - camera.viewportWidth) - foregroundTwoOffset);
        mapLayers.get("Ground").setOffsetX((camera.position.x - camera.viewportWidth) - groundOffset);
        mapLayers.get("Background").setOffsetX((camera.position.x - camera.viewportWidth) - backgroundOffset);

        if (player.currentState == Player.PlayerState.DEAD) return;

        //Increment Offset
        sunOffset+= (Player.getConstantSpeed() + (timeElapsed * 1f)) * 0.00001f;
        backgroundOffset += (Player.getConstantSpeed() + (timeElapsed * 1f)) * 0.0001f;
        foregroundOneOffset+= (Player.getConstantSpeed() + (timeElapsed * 1f)) * 0.01f;
        foregroundTwoOffset+= (Player.getConstantSpeed() + (timeElapsed * 1f)) * 0.001f;
        groundOffset+= (Player.getConstantSpeed() + (timeElapsed * 1f)) * 0.05f;

        //Reset Offset (1920 Width of the section of tile map)
        if (sunOffset >= 1920) sunOffset = 0;
        if (foregroundOneOffset >= 1920) foregroundOneOffset = 0;
        if (foregroundTwoOffset >= 1920) foregroundTwoOffset = 0;
        if (groundOffset >= 1920) groundOffset = 0;
        if (backgroundOffset >= 1920) backgroundOffset = 0;
    }

    public void newGame() {
        timeElapsed = 0f;
    }

    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
    }

}
