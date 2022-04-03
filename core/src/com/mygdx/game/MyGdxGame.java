package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.GrayFilter;

public class MyGdxGame extends ApplicationAdapter {

	public enum GameState { PLAYING, PAUSED, COMPLETE }

	private final float gravity = 5f;

	GameState gameState;

	TiledGameMap gameMap;

	//Map and Rendering
	SpriteBatch batch;
	SpriteBatch uiBatch;
	OrthographicCamera camera;

	//Player
	Player player;

	//Background Textures
	Texture backgroundGround;

	//Storage class for collision
	Rectangle tileRectangle;

	//UI textures
	Texture buttonSquareTexture;
	Texture buttonSquareDownTexture;
	Texture buttonLongTexture;
	Texture buttonLongDownTexture;

	//UI Buttons
	Button moveLeftButton;
	Button moveRightButton;
	Button moveUpButton;
	Button restartButton;

	//Just use this to only restart when the restart button is released instead of immediately as it's pressed
	boolean restartActive;

	@Override
	public void create () {


		//Camera
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w * 1.75f, h * 1.75f);
		camera.update();

		//Rendering
		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		player = new Player();
		gameMap = new TiledGameMap();

		//UI Textures
		buttonSquareTexture = new Texture("GUI/buttonSquare_blue.png");
		buttonSquareDownTexture = new Texture("GUI/buttonSquare_beige_pressed.png");
		buttonLongTexture = new Texture("GUI/buttonLong_blue.png");
		buttonLongDownTexture = new Texture("GUI/buttonLong_beige_pressed.png");

		//Buttons
		float buttonSize = h * 0.2f;
		moveLeftButton = new Button(0.0f, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
		moveRightButton = new Button(buttonSize + buttonSize / 5, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
		moveUpButton = new Button(buttonSize / 2 + buttonSize / 10, buttonSize + buttonSize / 10, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
		restartButton = new Button(w/2 - buttonSize*2, h * 0.2f, buttonSize*4, buttonSize, buttonLongTexture, buttonLongDownTexture);

		//Collision
		tileRectangle = new Rectangle();

		//Map Textures
		backgroundGround = new Texture("background/background_00.png");

		newGame();
	}

	@Override
	public void render () {

		player.dt = Gdx.graphics.getDeltaTime();

		//Game World
		update();

		//Clear the screen every frame before drawing.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameMap.render(camera);

		//Player
		player.updateCurrentPlayerState();

		//Apply camera and draw player
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		player.playerSprite.draw(batch);
		batch.end();

		//Draw UI
		uiBatch.begin();
		switch(gameState) {
			//if gameState is Running: Draw Controls
			case PLAYING:
				moveLeftButton.draw(uiBatch);
				moveRightButton.draw(uiBatch);
				moveUpButton.draw(uiBatch);
				break;
			case COMPLETE:
				restartButton.draw(uiBatch);
				break;
		}
		uiBatch.end();
	}


	public void update() {

		//Touch Input Info
		boolean checkTouch = Gdx.input.isTouched();
		int touchX = Gdx.input.getX();
		int touchY = Gdx.input.getY();

		switch(gameState) {

			case PLAYING:
				//Poll user for input
				moveLeftButton.update(checkTouch, touchX, touchY);
				moveRightButton.update(checkTouch, touchX, touchY);
				moveUpButton.update(checkTouch, touchX, touchY);

				int moveX = 0;
				int moveY = 0;
				if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || moveLeftButton.isDown) {
					moveLeftButton.isDown = true;
					moveX -= 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) || moveRightButton.isDown) {
					moveRightButton.isDown = true;
					moveX += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || moveUpButton.isDown) {
					moveUpButton.isDown = true;
					moveY += 1;
				}

				//TODO Check collision
				MapLayer collisionLayer = gameMap.tiledMap.getLayers().get("collision");
				TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

				//TODO Determine Character Movement Distance
				player.movePlayer(moveX, moveY, tileLayer);
				camera.translate(player.getConstantSpeed() * player.dt, 0f);
				camera.update();

//				//TODO Check movement against grid
//				if (player.getPlayerDelta().len2() > 0) { //Don't do anything if we're not moving
//					//Retrieve Collision layer
//					MapLayer collisionLayer = gameMap.tiledMap.getLayers().get("collision");
//					TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;
//
//					//TODO Determine bounds to check within
//					// Find top-right corner tile
//					int right = (int) Math.ceil(Math.max(player.playerSprite.getX() + player.playerSprite.getWidth(), player.playerSprite.getX() + player.playerSprite.getWidth() + player.getPlayerDelta().x));
//					int top = (int) Math.ceil(Math.max(player.playerSprite.getY() + player.playerSprite.getHeight(), player.playerSprite.getY() + player.playerSprite.getHeight() + player.getPlayerDelta().y));
//
//					// Find bottom-left corner tile
//					int left = (int) Math.floor(Math.min(player.playerSprite.getX(), player.playerSprite.getX() + player.getPlayerDelta().x));
//					int bottom = (int) Math.floor(Math.min(player.playerSprite.getY(), player.playerSprite.getY() + player.getPlayerDelta().y));
//
//					// Divide bounds by tile sizes to retrieve tile indices
//					right /= tileLayer.getTileWidth();
//					top /= tileLayer.getTileHeight();
//					left /= tileLayer.getTileWidth();
//					bottom /= tileLayer.getTileHeight();
//
//					//TODO Loop through selected tiles and correct by each axis
//					//EXTRA: Try counting down if moving left or down instead of counting up
//					for (int y = bottom; y <= top; y++) {
//						for (int x = left; x <= right; x++) {
//							TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
//							// If the cell is empty, ignore it
//							if (targetCell == null) continue;
//							// Otherwise correct against tested squares
//							tileRectangle.x = x * tileLayer.getTileWidth();
//							tileRectangle.y = y * tileLayer.getTileHeight();
//
//							player.playerDeltaRectangle.x = player.playerSprite.getX() + player.getPlayerDelta().x;
//							player.playerDeltaRectangle.y = player.playerSprite.getY();
//							if (tileRectangle.overlaps(player.playerDeltaRectangle)) player.getPlayerDelta().x = 0;
//
//							player.playerDeltaRectangle.x = player.playerSprite.getX();
//							player.playerDeltaRectangle.y = player.playerSprite.getY() + player.playerDelta.y;
//							if (tileRectangle.overlaps(player.playerDeltaRectangle)) player.getPlayerDelta().y = 0;
//						}
//					}
//
//				break;
//			}

		}

	}

	public void newGame() {
		gameState = GameState.PLAYING;

		player.dt = 0.0f;

		MapLayer objectLayer = gameMap.tiledMap.getLayers().get("Objects");

		//Set player and camera starting location
		RectangleMapObject playerObject = (RectangleMapObject) objectLayer.getObjects().get("Player");
		player.playerSprite.setCenter(playerObject.getRectangle().x, (playerObject.getRectangle().y + (playerObject.getRectangle().getHeight() * 1.12f)));
		camera.position.x = player.playerSprite.getX();
		camera.position.y = player.playerSprite.getY() * 4.75f; //TODO Optamise camera height when seting up world
		camera.update();

		restartActive = false;
	}

	@Override
	public void dispose () {
		batch.dispose();
		player.playerTexture.dispose();
		buttonSquareTexture.dispose();
		buttonSquareDownTexture.dispose();
		buttonLongTexture.dispose();
		buttonLongDownTexture.dispose();
	}
}
