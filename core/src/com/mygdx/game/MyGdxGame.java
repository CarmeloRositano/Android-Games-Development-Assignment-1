package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.w3c.dom.Text;


public class MyGdxGame extends ApplicationAdapter {

	public enum GameState { PLAYING, COMPLETE, PAUSED }

	public static final float MOVEMENT_SPEED = 200.0f;

	GameState gameState = GameState.PLAYING;

	//Map and rendering
	SpriteBatch batch;
	SpriteBatch uiBatch;
	OrthographicCamera camera;

	//Game Clock
	float dt;

	//Player Character
	Texture playerTexture;

	Texture playerWalkingTexture;
	private TextureRegion[] playerWalkingFrames;
	private Animation playerWalkingAnimation;

	Texture playerDyingTexture;
	private TextureRegion[] playerDyingFrames;
	private Animation playerDyingAnimation;

	Texture playerShootingTexture;
	private TextureRegion[] playerShootingFrames;
	private Animation playerShootingAnimation;

	private TextureRegion currentFrame;
	private float stateTime;

	//UI Buttons
	Button moveLeftButton;
	Button moveRightButton;
	Button jumpButton;

	Boolean restartActive;

	@Override
	public void create () {

		//Player Walking Texture Build
		int walkingFrameCol = 3;
		int WalkingFrameRow = 6;
		playerWalkingTexture = new Texture(Gdx.files.internal("player/moving.png"));
		batch = new SpriteBatch();
		TextureRegion[][] temp = TextureRegion.split(playerWalkingTexture, playerWalkingTexture.getWidth() / walkingFrameCol, playerWalkingTexture.getHeight() / WalkingFrameRow);
		playerWalkingFrames = new TextureRegion[walkingFrameCol * WalkingFrameRow];
		int index = 0;
		for (int i = 0; i < WalkingFrameRow; i++) {
			for (int j = 0; j < walkingFrameCol; j++) {
				playerWalkingFrames[index++] = temp[i][j];
			}
		}
		playerWalkingAnimation = new Animation(0.033f, playerWalkingFrames);

		//Player Shooting Texture Build
		playerDyingTexture = new Texture(Gdx.files.internal("player/shooting.png"));

		//Player Dying Texture Build
		playerShootingTexture = new Texture(Gdx.files.internal("player/dying.png"));

		stateTime = 0.0f;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stateTime += Gdx.graphics.getDeltaTime();

		currentFrame = (TextureRegion) playerWalkingAnimation.getKeyFrame(stateTime, true);

		batch.begin();
		batch.draw(currentFrame,1,1);
		batch.end();
	}
	
	@Override
	public void dispose () {

	}
}
