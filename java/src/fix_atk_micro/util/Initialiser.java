package fix_atk_micro.util;

import fix_atk_micro.Game;

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
					int minY = 10 * y;
					int[] row = ignoreCooldown[minX];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 1];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 2];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 3];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 4];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 5];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 6];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 7];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 8];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
					row = ignoreCooldown[minX + 9];
					row[minY] = -1000000;
					row[minY + 1] = -1000000;
					row[minY + 2] = -1000000;
					row[minY + 3] = -1000000;
					row[minY + 4] = -1000000;
					row[minY + 5] = -1000000;
					row[minY + 6] = -1000000;
					row[minY + 7] = -1000000;
					row[minY + 8] = -1000000;
					row[minY + 9] = -1000000;
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
					int minY = 10 * y;
					if (hasInitIgnoreCooldown && !ignoreCooldownBool[x][y]) {
						ignoreCooldownBool[x][y] = true;
						int[] row = ignoreCooldown[minX];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 1];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 2];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 3];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 4];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 5];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 6];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 7];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 8];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
						row = ignoreCooldown[minX + 9];
						row[minY] = -1000000;
						row[minY + 1] = -1000000;
						row[minY + 2] = -1000000;
						row[minY + 3] = -1000000;
						row[minY + 4] = -1000000;
						row[minY + 5] = -1000000;
						row[minY + 6] = -1000000;
						row[minY + 7] = -1000000;
						row[minY + 8] = -1000000;
						row[minY + 9] = -1000000;
					}
				}
			}
		}
	}
}
