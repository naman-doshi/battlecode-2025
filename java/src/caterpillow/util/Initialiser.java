package caterpillow.util;

import caterpillow.Game;

public class Initialiser {
	private static boolean hasInitIgnoreCooldown = false;
	private static boolean[][] ignoreCooldownBool;
	private static int[][] ignoreCooldown;
	public static int[][] getIgnoreCooldown() {
		hasInitIgnoreCooldown = true;
		ignoreCooldown = new int[60][60];
		ignoreCooldownBool = new boolean[6][6];
		int posX = Game.rc.getLocation().x - 5;
		int posY = Game.rc.getLocation().y - 5;
		for (int x = 5; x >= 0; x--) {
			for (int y = 5; y >= 0; y--) {
				if ((10 * x - posX) * (10 * x - posX) + (10 * y - posY) * (10 * y - posY) <= 200) {
					ignoreCooldownBool[x][y] = true;
					int minX = 10 * x;
					int maxX = 10 * x + x;
					int minY = 10 * y;
					int maxY = 10 * y + y;
					for (int tx = minX; tx < maxX; tx++) {
						int[] row = ignoreCooldown[tx];
						for (int ty = minY; ty < maxY; ty++) {
							row[ty] = -1000000;
						}
					}
				}
			}
		}
		return ignoreCooldown;
	}
	public static void upd() {
		int posX = Game.rc.getLocation().x - 5;
		int posY = Game.rc.getLocation().y - 5;
		for (int x = 5; x >= 0; x--) {
			for (int y = 5; y >= 0; y--) {
				if ((10 * x - posX) * (10 * x - posX) + (10 * y - posY) * (10 * y - posY) <= 200) {
					int minX = 10 * x;
					int maxX = 10 * x + x;
					int minY = 10 * y;
					int maxY = 10 * y + y;
					if (hasInitIgnoreCooldown && !ignoreCooldownBool[x][y]) {
						ignoreCooldownBool[x][y] = true;
						for (int tx = minX; tx < maxX; tx++) {
							int[] row = ignoreCooldown[tx];
							for (int ty = minY; ty < maxY; ty++) {
								row[ty] = -1000000;
							}
						}
					}
				}
			}
		}
	}
}
