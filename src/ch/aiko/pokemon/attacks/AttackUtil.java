package ch.aiko.pokemon.attacks;

import java.util.ArrayList;
import java.util.Comparator;

public class AttackUtil {

	public static ArrayList<Attack> registeredAttacks = new ArrayList<Attack>();

	public static void registerAttack(Attack a) {
		registeredAttacks.add(a);
	}

	public static void sort() {
		registeredAttacks.sort(new Comparator<Attack>() {
			public int compare(Attack o1, Attack o2) {
				for (int i = 0; i < o1.attackName.length() && i < o2.attackName.length(); i++) {
					if (o1.attackName.charAt(i) < o2.attackName.charAt(i)) return -1;
					if (o1.attackName.charAt(i) > o2.attackName.charAt(i)) return 1;
				}
				return 0;
			}
		});
	}

	public static Attack getAttack(String name) {
		for (int i = 0; i < registeredAttacks.size(); i++) {
			if (registeredAttacks.get(i).attackName.equalsIgnoreCase(name)) return registeredAttacks.get(i);
		}
		return null;
	}

}
