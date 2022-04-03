package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    public enum PlayerState { RUNNING, JUMPING, DYING, DEAD, SHOOTING }

    private static final float MOVEMENT_SPEED = 100.0f;

    private static final float GRAVITY = 98f;

    PlayerState currentPlayerState;

    Sprite playerSprite = new Sprite();

    Texture playerTexture;
    Vector2 playerDelta;
    Rectangle playerDeltaRectangle;
    Texture playerWalkingTexture;
    private TextureRegion[] playerWalkingFrames;

    private Animation playerWalkingAnimation;

    //Game Clock

    float dt;
    private TextureRegion currentFrame;

    public Player() {
        currentPlayerState = PlayerState.RUNNING;

        playerSprite.setSize(256,256);
        playerDelta = new Vector2();
        playerDeltaRectangle = new Rectangle(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());

        //Player Walking Texture Build
        int walkingFrameCol = 3;
        int WalkingFrameRow = 6;
        playerWalkingTexture = new Texture(Gdx.files.internal("player/moving.png"));
        TextureRegion[][] walkTemp = TextureRegion.split(playerWalkingTexture, playerWalkingTexture.getWidth() / walkingFrameCol, playerWalkingTexture.getHeight() / WalkingFrameRow);
        playerWalkingFrames = new TextureRegion[walkingFrameCol * WalkingFrameRow];
        int index = 0;
        for (int i = 0; i < WalkingFrameRow; i++) {
            for (int j = 0; j < walkingFrameCol; j++) {
                playerWalkingFrames[index++] = walkTemp[i][j];
            }
        }
        playerWalkingAnimation = new Animation(0.033f, playerWalkingFrames);

        updateCurrentPlayerState();


        dt = 0.0f;
    }

    public void updateCurrentPlayerState() {

        dt += Gdx.graphics.getDeltaTime();

        switch (currentPlayerState) {
            case RUNNING:
                currentFrame = (TextureRegion) playerWalkingAnimation.getKeyFrame(dt, true);
                playerSprite.setRegion(currentFrame);

                break;

            case DYING:
                //TODO If dying animation.isFinished()
                //currentPlayerState = PlayerState.DEAD;
                break;

            case JUMPING:
                //TODO If isOnGround() set player to RUNNING
                break;

            case SHOOTING:

                break;

        }
    }


    //Getters and Setters

    public PlayerState getCurrentPlayerState() { return currentPlayerState; }
    public TextureRegion getCurrentFrame() { return currentFrame; }

    public void setCurrentFrame(TextureRegion currentFrame) {
        this.currentFrame = currentFrame;
    }
    public Animation getPlayerWalkingAnimation() {
        return playerWalkingAnimation;
    }

    public void setPlayerWalkingAnimation(Animation playerWalkingAnimation) {
        this.playerWalkingAnimation = playerWalkingAnimation;
    }

    public static float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }

    public Vector2 getPlayerDelta() {
        return playerDelta;
    }

    public void setPlayerDeltaX(float playerDeltaX) {
        this.playerDelta.x = playerDeltaX;
    }

    public void setPlayerDeltaY(float playerDeltaY) {
        this.playerDelta.y = playerDeltaY;
    }

    public static float getGRAVITY() {
        return GRAVITY;
    }

}
