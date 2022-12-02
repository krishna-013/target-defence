package enemies;

import static helps.Constants.Enemies.ORC;

import managers.EnemyManager;

public class Orc extends Enemy {

	public Orc(float x, float y, int ID, EnemyManager enemyManager) {
		super(x, y, ID, ORC, enemyManager);
//		health = 50;
	}

}
