package scenes;

import java.awt.image.BufferedImage;

import main.Game;

public class GameScene {

	protected Game game;
	protected int animationIndex;
	protected int ANIMATION_SPEED = 15;
	protected int tick;

	public GameScene(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	protected boolean isAnimation(int spriteId) {
		return game.getTileManager().isSpriteAnimation(spriteId);
	}

	protected void updateTick() {
		tick++;
		if (tick >= ANIMATION_SPEED) {
			tick = 0;
			animationIndex++;
			if (animationIndex >= 4) {
				animationIndex = 0;
			}
		}
	}

	protected BufferedImage getSprite(int spriteId) {
		return game.getTileManager().getSprite(spriteId);
	}

	protected BufferedImage getSprite(int spriteId, int animationIndex) {
		return game.getTileManager().getAniSprite(spriteId, animationIndex);
	}
}
