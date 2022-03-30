package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {

    float x;
    float y;
    float w;
    float h;
    boolean isDown = false;

    Texture textureUp;
    Texture textureDown;

    public Button(Texture textureUp, Texture textureDown) {
        this.textureDown = textureDown;
        this.textureUp = textureUp;
    }

    public void update(float x, float y, float w, float h, boolean checkTouch, int touchX, int touchY) {
        isDown = false;

        if (checkTouch) {
            int touchHeight = Gdx.graphics.getHeight();

            if (touchX >= x && touchX <= x + w && touchHeight - touchY >= y && touchHeight - touchY <= y + h) {
                isDown = true;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (! isDown) {
            batch.draw(textureUp, x, y, w, h);
        } else {
            batch.draw(textureDown, x, y, w, h);
        }
    }

}
