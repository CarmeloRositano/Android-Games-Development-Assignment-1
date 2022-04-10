package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FlyingEnemyProjectile {

    private static final float GRAVITY = 98f;

    private final Texture texture = new Texture("air_enemy/projectile.png"); //Set here to stop lag from loading texture from drive
    Sprite sprite;
    Vector2 delta;
    float lifeTime;
    float lifeTimer;
    private boolean remove;

    /**
     * Builds the projectile that the flying enemies uses. Sets the position of self to given x, y
     * coordinates
     * @param x x coordinates for spawn
     * @param y y coordinates for spawn
     */
    public FlyingEnemyProjectile(float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        delta = new Vector2();
        lifeTime = 2f;
        lifeTimer = 0f;
        remove = false;
    }

    /**
     * Moves the projectile and accelerates it faster as it is in the air for longer, also counts
     * how long self has been alive for and determines if it should be removed or not.
     * @param dt Delta Time
     */
    public void move(float dt) {
        this.delta.y -= (GRAVITY * dt) / 3;

        sprite.translate(this.delta.x, this.delta.y);

        lifeTimer += dt;

        if (lifeTimer >= lifeTime) {
            remove = true;
        }

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

    /**
     * Returns true if hitting collision box of ground of player else false;
     * @param collisionLayer the collision data that is given through map class
     * @return whether projectile needs to be removed
     */
    public boolean shouldRemove(TiledMapTileLayer collisionLayer) {
        remove = collidesBottom(collisionLayer);
        return remove;
    }

    /**
     * checks if coordinates around self have any collision tiles and returns results
     * @param x x coordinates for spawn
     * @param y y coordinates for spawn
     * @param collisionLayer the collision data that is given through map class
     * @return if self is going to be blocked by any collision map tiles
     */
    private boolean isCellBlocked(float x, float y, TiledMapTileLayer collisionLayer) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null;
    }

    /**
     * checks cells around given coordinates on the given collisionlayer to determine if projectile
     * @param collisionLayer the collision data that is given through map class
     * @return if self is going to be blocked by any collision map tiles
     */
    public boolean collidesBottom(TiledMapTileLayer collisionLayer) {
        for(float step = 0; step < getHitBox().getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            if(isCellBlocked(getHitBox().getX() + step, getHitBox().getY(), collisionLayer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dispose of variables
     */
    public void dispose() {
        texture.dispose();
    }

}
