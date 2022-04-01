package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Player player;

	@Override
	public void create () {
		batch = new SpriteBatch();
		player = new Player();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);

		player.updateCurrentPlayerState();

		batch.begin();
		batch.draw(player.getCurrentFrame(), 1, 1);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
