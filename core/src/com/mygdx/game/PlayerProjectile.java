package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerProjectile {

    private static final float BULLET_SPEED = 1000f;
    private static final float GRAVITY = 98f;

    private final Texture texture = new Texture("player/projectile.png"); //Set here to stop lag from loading texture from drive
    Sprite sprite;
    Vector2 delta;
    float lifeTime;
    float lifeTimer;
    private boolean remove;

    public PlayerProjectile(float x, float y) {

        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        delta = new Vector2();
        lifeTime = 2f;
        lifeTimer = 0f;
        remove = false;
    }

    public void move(float dt) {
        this.delta.x = BULLET_SPEED * dt;
        this.delta.y -= (GRAVITY * dt) / 2;

        sprite.translate(this.delta.x, this.delta.y);

        lifeTimer += dt;

        if (lifeTimer >= lifeTime) {
            remove = true;
        }

    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    public Rectangle getHitBox() {
        return sprite.getBoundingRectangle();
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean shouldRemove(TiledMapTileLayer collisionLayer) {
        remove = collidesBottom(collisionLayer);
        return remove;
    }

    private boolean isCellBlocked(float x, float y, TiledMapTileLayer collisionLayer) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null;
    }

    public boolean collidesBottom(TiledMapTileLayer collisionLayer) {
        for(float step = 0; step < getHitBox().getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            if(isCellBlocked(getHitBox().getX() + step, getHitBox().getY(), collisionLayer)) {
                return true;
            }
        }
        return false;
    }
}

