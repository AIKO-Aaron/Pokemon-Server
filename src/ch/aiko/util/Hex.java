package ch.aiko.util;

public class Hex {
	
	public static final Character[] allowedChars = new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String DezToHex(long dez) {
		String hex = "";

		while (dez > 0) {
			hex = allowedChars[(int) (dez % 16)] + hex;
			dez /= 16;
		}

		return hex;
	}
	
	public static int HexToDez(String hex) {
		int dez = 0;
		for (int i = 0; i < hex.length(); i++) {
			int j = getPointAt(allowedChars, hex.charAt(i));
			dez += Math.pow(16, i) * j;
		}
		return dez;
	}
	
	private static int getPointAt(Object[] objects, Object o) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].equals(o)) return i;
		}
		return -1;
	}
}
