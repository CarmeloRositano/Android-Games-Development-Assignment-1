package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Landmine {

    Texture texture;
    Sprite sprite;
    Vector2 delta;
    TiledGameMap gameMap;
    boolean isAlive;

    public Landmine(float x, float y, TiledGameMap gameMap) {

        isAlive = true;
        this.gameMap = gameMap;
        texture = new Texture("ground_enemy/landmine.png");
        sprite = new Sprite(texture);
        sprite.setSize(sprite.getWidth() * 3f, sprite.getHeight() * 3f);
        delta = new Vector2();
        sprite.setPosition(x, y);

    }

    public void move(float dt, MyGdxGame.GameState gameState) {
        if(gameState != MyGdxGame.GameState.PLAYING) return;
        System.out.println("LANDMINE X: " + sprite.getX());
        this.delta.x = -(((Player.getConstantSpeed() + (gameMap.timeElapsed)) * 0.05f) / Gdx.graphics.getDeltaTime() * dt);

        sprite.translate(delta.x, delta.y);
    }

    /**
     * Draws the sprite to screen on the provided Batch
     * @param batch The Batch to draw the text to screen
     */
    public void draw(SpriteBatch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    /**
     * Creates a custom hit box and returns it
     * @return  Rectangle that is the hit box
     */
    public Rectangle getHitBox() {
        return sprite.getBoundingRectangle();
    }

    public void setDead() {
        isAlive = false;
        sprite.setAlpha(0f);
    }

    public void setAlive() {
        isAlive = true;
        sprite.setAlpha(1f);
    }

    /**
     * Dispose of variables
     */
    public void dispose() {
        texture.dispose();
    }

}
