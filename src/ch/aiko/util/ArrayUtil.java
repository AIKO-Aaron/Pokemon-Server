package ch.aiko.util;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayUtil {

	public static String[] delete(String[] old, int indexToDelete) {
		if (old.length <= indexToDelete) return old;

		int index = 0;
		String[] newString = new String[old.length - 1];
		for (int i = 0; i < old.length; i++) {
			if (i == indexToDelete) continue;
			newString[index] = old[i];
			index++;
		}

		return newString;
	}

	public static int getPointAt(Object[] objects, Object o) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].equals(o)) return i;
		}
		return -1;
	}

	public static String combineToFile(String[] array) {
		String s = "";
		for (String l : array)
			s += l + "\n";
		return s;
	}

	public static Object[] combine(Object[] a1, Object[] a2) {
		int nLength = a1.length + a2.length;
		Object[] a3 = new Object[nLength];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	public static Object[] combine(Object[] a1, Object[]... arrays) {
		Object[] ret = a1.clone();
		for (Object[] i : arrays) {
			ret = combine(ret, i);
		}
		return ret;
	}

	public static int[] combine(int[] a1, int[] a2) {
		int nLength = a1.length + a2.length;
		int[] a3 = new int[nLength];
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

	public static int[] combine(int[] array1, int[]... arrays) {
		int[] ret = array1.clone();
		for (int[] i : arrays) {
			ret = combine(ret, i);
		}
		return ret;
	}

	public static ArrayList<Object> toList(Object[] o) {
		ArrayList<Object> ret = new ArrayList<Object>();

		for (int i = 0; i < o.length; i++) {
			ret.add(o[i]);
		}

		return ret;
	}

	public static ArrayList<Short> toList(short[] data) {
		ArrayList<Short> ret = new ArrayList<Short>();

		for (int i = 0; i < data.length; i++) {
			ret.add(data[i]);
		}

		return ret;
	}

	public static short[] toArray(Short[] data) {
		short[] ret = new short[data.length];
		for (int i = 0; i < data.length; i++)
			ret[i] = data[i];
		return ret;
	}

	public static short[] toArray(ArrayList<Short> data) {
		short[] ret = new short[data.size()];
		Iterator<Short> it = data.iterator();
		for (int i = 0; i < ret.length; i++)
			ret[i] = it.next().shortValue();

		return ret;
	}
}
