package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GroundEnemy {

    public enum GroundEnemyState { MOVING, DYING, DEAD;};
    private static float CONSTANT_SPEED = 50f;

    Sprite groundEnemySprite;
    Vector2 groundEnemyDelta;
    private TextureRegion currentFrame;

    //Enemy Walking
    private final Texture groundEnemyWalkingTexture = new Texture("ground_enemy/moving.png"); //Set here to stop lag from loading texture from drive
    private TextureRegion[] groundEnemyWalkingFrames;
    private Animation groundEnemyWalkingAnimation;

    //Enemy Dying
    private final Texture groundEnemyDyingTexture = new Texture("ground_enemy/dying.png"); //Set here to stop lag from loading texture from drive
    private TextureRegion[] groundEnemyDyingFrames;
    private Animation groundEnemyDyingAnimation;

    boolean isAlive;

    float stateTime;

    GroundEnemyState groundEnemyState;
    public GroundEnemy(float x, float y) {
        groundEnemyState = GroundEnemyState.MOVING;

        isAlive = true;
        stateTime = 0.0f;

        groundEnemySprite = new Sprite();
        groundEnemyDelta = new Vector2();

        System.out.println(groundEnemySprite.getHeight() + "\n" + groundEnemySprite.getWidth());
        groundEnemySprite.setSize(128, 128);

//        groundEnemySprite.setSize(groundEnemySprite.getWidth() * 4, groundEnemySprite.getHeight() * 4);

        //Walking
        int frameCol = 6;
        int frameRow = 3;
        TextureRegion[][] walkTemp = TextureRegion.split(groundEnemyWalkingTexture, groundEnemyWalkingTexture.getWidth() / frameCol,
                groundEnemyWalkingTexture.getHeight() / frameRow);
        groundEnemyWalkingFrames = new TextureRegion[frameCol * frameRow];
        int index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                groundEnemyWalkingFrames[index++] = walkTemp[i][j];
            }
        }
        groundEnemyWalkingAnimation = new Animation(1f/30f, groundEnemyWalkingFrames);
        //Dying
        frameCol = 6;
        frameRow = 5;
        TextureRegion[][] dyingTemp = TextureRegion.split(groundEnemyDyingTexture, groundEnemyDyingTexture.getWidth() / frameCol,
                groundEnemyDyingTexture.getHeight() / frameRow);
        groundEnemyDyingFrames = new TextureRegion[(frameCol * frameRow) - 4];
       index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                if(index < 26)
                groundEnemyDyingFrames[index++] = dyingTemp[i][j];
            }
        }
        groundEnemyDyingAnimation = new Animation(1f/30f, groundEnemyDyingFrames);

        groundEnemySprite.setPosition(x, y);

        updateCurrentState();
    }

    public void updateCurrentState() {

        stateTime += Gdx.graphics.getDeltaTime();

        switch (groundEnemyState) {
            case MOVING:
                currentFrame = (TextureRegion) groundEnemyWalkingAnimation.getKeyFrame(stateTime, true);
                groundEnemySprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) groundEnemyDyingAnimation.getKeyFrame(stateTime, false);
                if(groundEnemyDyingAnimation.isAnimationFinished(stateTime)) {
                    groundEnemyState = GroundEnemyState.DEAD;
                    break;
                }
                groundEnemySprite.setRegion(currentFrame);
                break;

            case DEAD:
                CONSTANT_SPEED = 150f;
                break;
        }
        groundEnemySprite.setRegion(currentFrame);
    }

    public void groundEnemyMovement(float dt) {
        this.groundEnemyDelta.x = CONSTANT_SPEED * dt;
        groundEnemySprite.translate(groundEnemyDelta.x, groundEnemyDelta.y);
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        groundEnemySprite.draw(batch);
        batch.end();
    }

    public boolean shouldRemove(Player player) {
        if(player.playerDelta.x > Gdx.graphics.getWidth() && !isAlive) {
            return true;
        }
        return false;
    }

    public void dispose() {
        groundEnemyDyingTexture.dispose();
        groundEnemyWalkingTexture.dispose();
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

}
