package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GroundEnemy{

    public enum GroundEnemyState { MOVING, DYING, DEAD }
    private float constantSpeed = 100f;

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
    float stateTime;

    GroundEnemyState groundEnemyState;
    public GroundEnemy(float x, float y) {
        groundEnemyState = GroundEnemyState.MOVING;
        stateTime = 0.0f;

        groundEnemySprite = new Sprite();
        groundEnemyDelta = new Vector2();

        //Walking
        int frameCol = 6;
        int frameRow = 3;

        groundEnemySprite.setSize(groundEnemyWalkingTexture.getWidth() / frameCol * 0.7f
                ,groundEnemyWalkingTexture.getHeight() / frameRow * 0.7f);

        TextureRegion[][] walkTemp = TextureRegion.split(groundEnemyWalkingTexture, groundEnemyWalkingTexture.getWidth() / frameCol,
                groundEnemyWalkingTexture.getHeight() / frameRow);
        groundEnemyWalkingFrames = new TextureRegion[frameCol * frameRow];
        int index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                groundEnemyWalkingFrames[index++] = walkTemp[i][j];
            }
        }
        groundEnemyWalkingAnimation = new Animation(1f/30f, (Object[]) groundEnemyWalkingFrames);
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
        groundEnemyDyingAnimation = new Animation(1f/30f, (Object[]) groundEnemyDyingFrames);

        groundEnemySprite.setPosition(x, y);

    }

    public void updateCurrentState(TiledGameMap gameMap) {

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
                    //make enemy move at the speed of the ground when dead
                    constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed * 1f)) * 0.05f) / Gdx.graphics.getDeltaTime();
                    break;
                }
                groundEnemySprite.setRegion(currentFrame);
                break;

            case DEAD:
                //make enemy move at the speed of the ground when dead
                constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed * 1f)) * 0.05f) / Gdx.graphics.getDeltaTime();
                break;
        }
    }

    public void groundEnemyMovement(float dt) {
        this.groundEnemyDelta.x = -constantSpeed * dt;
        groundEnemySprite.translate(groundEnemyDelta.x, groundEnemyDelta.y);
        if(groundEnemyState == GroundEnemyState.DYING) {
            groundEnemySprite.translateY(-0.8f);
        }

    }

    public Rectangle getHitBox() {
        return new Rectangle(groundEnemySprite.getX() + groundEnemySprite.getWidth() * 0.15f,
                groundEnemySprite.getY(),
                groundEnemySprite.getWidth() * 0.7f,
                groundEnemySprite.getHeight() * 0.6f);
    }


    public void draw(SpriteBatch batch) {
        batch.begin();
        groundEnemySprite.draw(batch);
        batch.end();
    }

    public void setDying() {
        groundEnemyState = GroundEnemyState.DYING;
        stateTime = 0f;
    }

    public void setAlive() {
        groundEnemyState = GroundEnemyState.MOVING;
        stateTime = 0f;
        constantSpeed = 100f;
    }

    public void dispose() {
        groundEnemyDyingTexture.dispose();
        groundEnemyWalkingTexture.dispose();
    }

}
