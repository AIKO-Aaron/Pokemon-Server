package ch.aiko.util;

public class ASCII {

	/**
	 * The Character 'A'
	 */
	public static final int A = 0x41;
	/**
	 * The Character 'B'
	 */
	public static final int B = 0x42;
	/**
	 * The Character 'C'
	 */
	public static final int C = 0x43;
	/**
	 * The Character 'D'
	 */
	public static final int D = 0x44;
	/**
	 * The Character 'E'
	 */
	public static final int E = 0x45;
	/**
	 * The Character 'F'
	 */
	public static final int F = 0x46;
	/**
	 * The Character 'G'
	 */
	public static final int G = 0x47;
	/**
	 * The Character 'H'
	 */
	public static final int H = 0x48;
	/**
	 * The Character 'I'
	 */
	public static final int I = 0x49;
	/**
	 * The Character 'J'
	 */
	public static final int J = 0x4A;
	/**
	 * The Character 'K'
	 */
	public static final int K = 0x4B;
	/**
	 * The Character 'L'
	 */
	public static final int L = 0x4C;
	/**
	 * The Character 'M'
	 */
	public static final int M = 0x4D;
	/**
	 * The Character 'N'
	 */
	public static final int N = 0x4E;
	/**
	 * The Character 'O'
	 */
	public static final int O = 0x4F;
	/**
	 * The Character 'P'
	 */
	public static final int P = 0x50;
	/**
	 * The Character 'Q'
	 */
	public static final int Q = 0x51;
	/**
	 * The Character 'R'
	 */
	public static final int R = 0x52;
	/**
	 * The Character 'S'
	 */
	public static final int S = 0x53;
	/**
	 * The Character 'T'
	 */
	public static final int T = 0x54;
	/**
	 * The Character 'U'
	 */
	public static final int U = 0x55;
	/**
	 * The Character 'V'
	 */
	public static final int V = 0x56;
	/**
	 * The Character 'W'
	 */
	public static final int W = 0x57;
	/**
	 * The Character 'X'
	 */
	public static final int X = 0x58;
	/**
	 * The Character 'Y'
	 */
	public static final int Y = 0x59;
	/**
	 * The Character 'Z'
	 */
	public static final int Z = 0x5a;

	public static final int a = 0x61;
	public static final int b = 0x62;
	public static final int c = 0x63;
	public static final int d = 0x64;
	public static final int e = 0x65;
	public static final int f = 0x66;
	public static final int g = 0x67;
	public static final int h = 0x68;
	public static final int i = 0x69;
	public static final int j = 0x6A;
	public static final int k = 0x6B;
	public static final int l = 0x6C;
	public static final int m = 0x6D;
	public static final int n = 0x6E;
	public static final int o = 0x6F;
	public static final int p = 0x70;
	public static final int q = 0x71;
	public static final int r = 0x72;
	public static final int s = 0x73;
	public static final int t = 0x74;
	public static final int u = 0x75;
	public static final int v = 0x76;
	public static final int w = 0x77;
	public static final int x = 0x78;
	public static final int y = 0x79;
	public static final int z = 0x7a;

	public static final int ß = 0xDF;
	public static final int ö = 0xF6;
	public static final int ä = 0xE4;
	public static final int ü = 0xFC;
	public static final int Ö = 0xD6;
	public static final int Ä = 0xC4;
	public static final int Ü = 0xDC;

	public static final int MINUS = 0x2D;
	public static final int PLUS = 0x2B;

	public static final int NUM_1 = 0x31;
	public static final int NUM_2 = 0x32;
	public static final int NUM_3 = 0x33;
	public static final int NUM_4 = 0x34;
	public static final int NUM_5 = 0x35;
	public static final int NUM_6 = 0x36;
	public static final int NUM_7 = 0x37;
	public static final int NUM_8 = 0x38;
	public static final int NUM_9 = 0x39;
	public static final int NUM_0 = 0x3A;

	public static final int EXC_MARK = 0x21;
	public static final int SPACE = 0x20;
	public static final int DOT = 0x2E;
	public static final int COMMA = 0x2C;
	public static final int NEW_LINE = 0x0A;
	public static final int CARRIAGE_RETURN = 0x0D;
	public static final int HALF = 0xBD;
	public static final int QUESTION_MARK = 0x3F;

	public static final int BRACKET_ON = 0x28;
	public static final int BRACKET_OFF = 0x29;

	public static final int CURLY_BRACKET_ON = 0x7B;
	public static final int CURLY_BRACKET_OFF = 0x7D;

	public static final int BOX_BRACKET_ON = 0x5B;
	public static final int BOX_BRACKET_OFF = 0x5D;

	public static final int NULL = 0x00;

	public static String getHex(Character c) {
		String s = "";
		try {
			int i = ASCII.class.getField("" + c).getInt(ASCII.class);
			s = Hex.DezToHex(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static int getDez(Character c) {
		int i = ASCII.NULL;
		try {
			if (c.equals('\n')) return ASCII.NEW_LINE;
			if (c.equals(' ')) return ASCII.SPACE;
			if (c.equals('.')) return ASCII.DOT;
			if (c.equals(',')) return ASCII.COMMA;
			if (c.equals('½')) return ASCII.HALF;
			if (c.equals('-') || c.equals('–')) return ASCII.MINUS;
			if (c.equals('+')) return ASCII.PLUS;
			if (c.equals('!')) return ASCII.EXC_MARK;
			if (c.equals('?')) return ASCII.QUESTION_MARK;
			if (c.equals('(')) return ASCII.BRACKET_ON;
			if (c.equals(')')) return ASCII.BRACKET_OFF;
			if (c.equals('{')) return ASCII.CURLY_BRACKET_ON;
			if (c.equals('}')) return ASCII.CURLY_BRACKET_OFF;
			if (c.equals('[')) return ASCII.BOX_BRACKET_ON;
			if (c.equals(']')) return ASCII.BOX_BRACKET_OFF;

			if (c.equals('0')) return ASCII.NUM_0;
			if (c.equals('1')) return ASCII.NUM_1;
			if (c.equals('2')) return ASCII.NUM_2;
			if (c.equals('3')) return ASCII.NUM_3;
			if (c.equals('4')) return ASCII.NUM_4;
			if (c.equals('5')) return ASCII.NUM_5;
			if (c.equals('6')) return ASCII.NUM_6;
			if (c.equals('7')) return ASCII.NUM_7;
			if (c.equals('8')) return ASCII.NUM_8;
			if (c.equals('9')) return ASCII.NUM_9;

			i = ASCII.class.getField("" + c).getInt(ASCII.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	public static int[] getInts(String s) {
		char[] c = s.toCharArray();
		int[] codes = new int[c.length];
		for (int i = 0; i < c.length; i++) {
			codes[i] = getDez(c[i]);
		}
		return codes;
	}

	public static char getChar(int ch) {
		return (char) ch;
	}
	
	public static String getString(int[] asciiCodes) {
		char[] chars = new char[asciiCodes.length];
		for (int i = 0; i < asciiCodes.length; i++) {
			chars[i] = getChar(asciiCodes[i]);
		}
		return new String(chars);
	}

}
