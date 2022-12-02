package enemies;

import static helps.Constants.Enemies.WOLF;

import managers.EnemyManager;

public class Wolf extends Enemy {

	public Wolf(float x, float y, int ID, EnemyManager enemyManager) {
		super(x, y, ID, WOLF, enemyManager);
	}

}
