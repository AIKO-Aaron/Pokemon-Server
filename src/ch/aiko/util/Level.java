package ch.aiko.util;

public enum Level {

	
	INFO(0, "[INFO]", new printer(){public void println(String s){System.out.println(s);} public void print(String s){System.out.print(s);}}), 
	WARNING(1, "[WARNING]", new printer(){public void println(String s){System.out.println(s);} public void print(String s){System.out.print(s);}}), 
	ERROR(2, "[ERROR]", new printer(){public void println(String s){System.err.println(s);} public void print(String s){System.err.print(s);}});
	
	private String name;
	private int level;
	private printer r;
	
	private Level(int level, String name, printer r) {
		this.level = level;
		this.name = name;
		this.r = r;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getLevelName() {
		return name;
	}
	
	public void println(String s) {
		r.println(s);
	}
	
	public void print(String s) {
		r.print(s);
	}
	
	private interface printer {
		public void println(String s);
		public void print(String s);
	}
	
}
