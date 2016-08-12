package ch.aiko.pokemon.attacks;

public class Attack {

	public String attackName;
	public int attackDamage, backFire;
	public Type type;

	public static void init() {
		new Attack("Verzweifler").setDamage(10).setBackFire(10);
		new Attack("Tackle").setDamage(10);
		new Attack("Flammenwurf").setDamage(60).setType(Type.FIRE);
		new Attack("Glut").setDamage(10).setType(Type.FIRE);
	}

	public Attack(String name) {
		String[] np = name.split(" ");
		attackName = "";
		for (String n : np)
			if (n != null) attackName += n.substring(0, 1).toUpperCase() + n.substring(1).toLowerCase() + (!name.endsWith(n) ? " " : "");
		AttackUtil.registerAttack(this);
	}

	public Attack setDamage(int d) {
		this.attackDamage = d;
		return this;
	}

	public Attack setBackFire(int d) {
		backFire = d;
		return this;
	}

	public Attack setType(Type t) {
		type = t;
		return this;
	}

}
