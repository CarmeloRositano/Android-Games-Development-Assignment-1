package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {

    float x;
    float y;
    float w;
    float h;
    boolean isDown = false;
    boolean isDownPrev = false;

    Texture textureUp;
    Texture textureDown;

    //Text
    BitmapFont font;

    public Button(float x, float y, float w, float h, Texture textureUp, Texture textureDown) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.textureUp = textureUp;
        this.textureDown = textureDown;

        font = new BitmapFont();
    }

    public void update(boolean checkTouch, int touchX, int touchY) {
        isDownPrev = isDown;
        isDown = false;

        if (checkTouch) {
            int h2 = Gdx.graphics.getHeight();


            if (touchX >= x && touchX <= x + w && h2 - touchY >= y && h2 - touchY <= y + h) {
                isDown = true;
            }
        }
    }

    public void addText(String text, Batch batch) {
        font.draw(batch, text, x + (w * 0.35f), y + h / 1.4f);
        font.getData().setScale(w / 100);
    }

    public void draw(SpriteBatch batch) {
        if (! isDown) {
            batch.draw(textureUp, x, y, w, h);
        } else {
            batch.draw(textureDown, x, y, w, h);
        }
    }


    public void dispose() {

    }
}
