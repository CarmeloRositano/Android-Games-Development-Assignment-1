package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerProjectile {

    private static final float BULLET_SPEED = 1000f;

    private final Texture bulletTexture = new Texture("player/projectile.png"); //Set here to stop lag from loading texture from drive
    Sprite bulletSprite;
    Vector2 bulletDelta;
    float bulletLifeTime;
    float bulletLifeTimer;

    private boolean remove;

    public PlayerProjectile(float x, float y) {

        bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setPosition(x, y);
        bulletDelta = new Vector2();
        bulletLifeTime = 2f;
        bulletLifeTimer = 0f;
        remove = false;
    }

    public void projectileMovement(float dt) {
        this.bulletDelta.x = BULLET_SPEED * dt;

        bulletSprite.translate(this.bulletDelta.x, this.bulletDelta.y);

        bulletLifeTimer += dt;

        if (bulletLifeTimer >= bulletLifeTime) {
            remove = true;
        }

    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        bulletSprite.draw(batch);
        batch.end();
    }

    public Rectangle getHitBox() {
        return bulletSprite.getBoundingRectangle();
    }

    public void dispose() {
        bulletTexture.dispose();
    }

    public boolean shouldRemove() {
        return remove;
    }
}

