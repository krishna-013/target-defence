package helps;

public class Constants {

	public static class Projectiles {
		public static final int ARROW = 0;
		public static final int CHAINS = 1;
		public static final int BOMB = 2;

		public static float GetSpeed(int type) {
			switch (type) {
			case ARROW:
				return 8f;
			case BOMB:
				return 4f;
			case CHAINS:
				return 6f;
			}
			return 0f;
		}
	}

	public static class Towers {
		public static final int CANON = 0;
		public static final int ARCHER = 1;
		public static final int WIZARD = 2;

		public static int GetTowerCost(int towerType) {
			switch (towerType) {
			case CANON:
				return 65;
			case ARCHER:
				return 35;
			case WIZARD:
				return 50;
			}
			return 0;
		}

		public static String GetName(int towerType) {
			switch (towerType) {
			case CANON:
				return "Cannon";
			case ARCHER:
				return "Archer";
			case WIZARD:
				return "Wizard";
			}
			return "";
		}

		public static int GetDefaultDmg(int towerType) {
			switch (towerType) {
			case CANON:
				return 15;
			case ARCHER:
				return 5;
			case WIZARD:
				return 0;
			}
			return 0;
		}

		public static float GetDefaultRange(int towerType) {
			switch (towerType) {
			case CANON:
				return 80;
			case ARCHER:
				return 120;
			case WIZARD:
				return 100;
			}
			return 0;
		}

		public static float GetDefaultCooldown(int towerType) {
			switch (towerType) {
			case CANON:
				return 120;
			case ARCHER:
				return 30;
			case WIZARD:
				return 40;
			}
			return 0;
		}
	}

	public static class Direction {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}

	public static class Tiles {
		public static final int WATER_TILE = 0;
		public static final int GRASS_TILE = 1;
		public static final int ROAD_TILE = 2;
	}

	public static class Enemies {
		public static final int ORC = 0;
		public static final int BAT = 1;
		public static final int KNIGHT = 2;
		public static final int WOLF = 3;

		public static int GetReward(int enemyType) {
			switch (enemyType) {
			case ORC:
				return 5;
			case BAT:
				return 5;
			case KNIGHT:
				return 25;
			case WOLF:
				return 10;
			}
			return 0;
		}

		public static float GetSpeed(int enemyType) {
			switch (enemyType) {
			case ORC:
				return 0.5f;
			case BAT:
				return 0.7f;
			case KNIGHT:
				return 0.45f;
			case WOLF:
				return 0.85f;
			}
			return 0f;
		}

		public static int GetStartHealth(int enemyType) {
			switch (enemyType) {
			case ORC:
				return 85;
			case BAT:
				return 100;
			case KNIGHT:
				return 350;
			case WOLF:
				return 120;
			}
			return 0;
		}
	}

}
