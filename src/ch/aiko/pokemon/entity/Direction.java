package ch.aiko.pokemon.entity;

public enum Direction {

	DOWN(0),
	UP(1),
	LEFT(2),
	RIGHT(3);

	private int direction;

	private Direction(int dir) {
		direction = dir;
	}

	public int getDir() {
		return direction;
	}

	public Direction getOpposite() {
		return direction == 0 ? UP : direction == 1 ? DOWN : direction == 2 ? RIGHT : LEFT;
	}
	
	public static Direction getDirection(int dir) {
		switch(dir) {
			case 0: return DOWN;
			case 1: return UP;
			case 2: return LEFT;
			case 3: return RIGHT;
		}
		return DOWN;
	}
}
