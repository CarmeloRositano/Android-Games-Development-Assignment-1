package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {

    public enum PlayerState { RUNNING, JUMPING, DYING, DEAD, SHOOTING }

    PlayerState currentPlayerState;
    int characterX;
    int characterY;

    Texture playerWalkingTexture;
    private TextureRegion[] playerWalkingFrames;
    private Animation playerWalkingAnimation;

    //Game Clock
    float dt;

    private TextureRegion currentFrame;

    public Player() {
        currentPlayerState = PlayerState.RUNNING;

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
        dt = 0.0f;
    }

    public void updateCurrentPlayerState() {

        dt += Gdx.graphics.getDeltaTime();

        switch (currentPlayerState) {
            case RUNNING:
                System.out.println(dt);
                currentFrame = (TextureRegion) playerWalkingAnimation.getKeyFrame(dt, true);
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

}
