package ch.aiko.util;

import java.io.File;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Log extends PrintStream {

	private boolean hasOutputFile = true;
	private boolean hasConsoleOutput = true;
	private String className = null;

	private File file;
	private static final Level DEFAULT = Level.INFO;

	private static Log log1, log2, log3;
	private Logger func;

	/**
	 * Creates a new Log instance with the output File s/currenttime in your ClassPath
	 * 
	 * @param s
	 *            The path to the output File in you classpath. The outputfile's name will be the current time
	 */
	public Log(String s) {
		super(System.out, true);
		long t = System.currentTimeMillis();
		file = FileUtil.LoadFileInClassPath("/logs/" + s + "/" + t + ".log");
		if (log1 == null) log1 = this;
	}

	/**
	 * Creates a new Log instance with the output File s/currenttime in your ClassPath
	 * 
	 * @param s
	 *            The path to the output File in you classpath. The outputfile's name will be the current time
	 */
	public Log(String s, Logger log) {
		super(System.out, true);
		func = log;
		long t = System.currentTimeMillis();
		file = FileUtil.LoadFileInClassPath("/logs/" + s + "/" + t + ".log");
		if (log1 == null) log1 = this;
	}

	/**
	 * Creates a new Log instance with the output File s/a in your ClassPath
	 * 
	 * @param s
	 *            The directory of the File (in your classpath)
	 * @param a
	 *            The filename
	 */
	public Log(String s, String a) {
		super(System.out, true);
		file = FileUtil.LoadFileInClassPath("/logs/" + s + "/" + a + ".log");
		if (log1 == null) log1 = this;
	}

	/**
	 * Creates a new Log instance without log file output, but with [classname] in front of the message
	 * 
	 * @param c
	 *            The class this object is held by
	 */
	public Log(Class<?> c) {
		super(System.out, true);
		className = c.getSimpleName();
		hasOutputFile = false;
	}

	/**
	 * Creates a new Log instance without log file output, but with [classname] in front of the message
	 * 
	 * @param c
	 *            The class this object is held by
	 */
	public Log(Class<?> c, Logger log) {
		super(System.out, true);
		func = log;
		className = c.getSimpleName();
		hasOutputFile = false;
	}

	/**
	 * Creates a new Log instance without log file output, but with [classname] in front of the message
	 * 
	 * @param c
	 *            The class this object is held by
	 */
	public Log(Class<?> c, String s) {
		super(System.out, true);
		className = c.getSimpleName();
		long t = System.currentTimeMillis();
		file = FileUtil.LoadFileInClassPath("/logs/" + s + "/" + t + ".log");
		hasOutputFile = true;
	}

	/**
	 * Creates a new Log instance with the output File filePath/currenttime in your ClassPath
	 * 
	 * @param filePath
	 *            The Path to the File. currenttime means the time that currently is.
	 * @param hasConsoleOutput
	 *            if the Log should print the log to the console in eclipse.
	 */
	public Log(String filePath, boolean hasConsoleOutput) {
		super(System.out, true);
		long t = System.currentTimeMillis();
		file = FileUtil.LoadFileInClassPath("/logs/" + filePath + "/" + t + ".log");
		if (log3 == null) log3 = this;
	}

	/**
	 * Creates a new Log instance with the output File filePath/t in your ClassPath
	 * 
	 * @param filePath
	 *            The Path to the File. t is the second argument.
	 * @param t
	 *            The filename
	 * @param hasConsoleOutput
	 *            if the Log should print the log to the console in eclipse.
	 */
	public Log(String filePath, String t, boolean hasConsoleOutput) {
		super(System.out, true);
		file = FileUtil.LoadFileInClassPath("/logs/" + filePath + "/" + t + ".log");
		if (log3 == null) log3 = this;
	}

	/**
	 * 
	 * Creates new Log without file output. It just prints everything into the Console. It also adds [Time:Time][Level] in front of the Text
	 * 
	 */
	public Log() {
		super(System.out, true);
		hasOutputFile = false;
		if (log2 == null) log2 = this;
	}

	/**
	 * Get a Log instance, if you already created one, which has console and file output, it will get this. Else it will return a new instance, with the output File "Logs/Newest.log" .
	 * 
	 * @return A Instance of Log
	 */
	public static Log getDefaultLog() {
		return log1 != null ? log1 : (log1 = new Log("Logs", "Newest"));
	}

	/**
	 * Get a Log instance, if you already created one, which has only console output, it will get this. Else it will return a new instance.
	 * 
	 * @return A Instance of Log
	 */
	public static Log getOnlyConsoleLog() {
		return log2 != null ? log2 : new Log();
	}

	/**
	 * Get a Log instance, if you already created one, which has only File output, it will get this. Else it will return a new instance, with the output File "Logs/currenttime.log" currenttime is the current time.
	 * 
	 * @return A Instance of Log
	 */
	public static Log getOnlyFileLog() {
		return log3 != null ? log3 : new Log("Logs", false);
	}

	/**
	 * Prints a string to the log. The Level represents the importance. If the Level is error, the console will print it in the error Console
	 * 
	 * @param s
	 *            The message to print
	 * @param l
	 *            The Level of the Message
	 */
	public void print(String s, Level l) {
		Calendar cal = Calendar.getInstance();
		String time = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", cal.get(Calendar.MINUTE)) + ":" + String.format("%02d", cal.get(Calendar.SECOND));
		s = "[" + time + "]" + (className != null ? "[" + className + "]" : "") + l.getLevelName() + " " + s;

		if (func != null) func.log(s);
		if (hasConsoleOutput) l.print(s);
		if (hasOutputFile) FileUtil.AddToFile(s, file);
	}

	/**
	 * Prints a string to the log and adds a new Line. The Level represents the importance. If the Level is error, the console will print it in the error Console
	 * 
	 * @param s
	 *            The message to print
	 * @param l
	 *            The Level of the Message
	 */
	public void println(String s, Level l) {
		print(s + "\n", l);
	}

	/**
	 * Prints a string to the log. The default Level is Info
	 * 
	 * @param s
	 *            The message to print
	 */
	public void print(String s) {
		print(s, DEFAULT);
	}

	/**
	 * Prints a float to the log. The default Level is Info
	 * 
	 * @param s
	 *            The message to print
	 */
	public void print(float s) {
		print(s + "", DEFAULT);
	}

	/**
	 * Prints a string to the log and adds a new line. The default Level is Info
	 * 
	 * @param s
	 *            The message to print
	 */
	public void println(String s) {
		println(s, DEFAULT);
	}

	/**
	 * Prints a float to the log and adds a new line. The default Level is Info
	 * 
	 * @param s
	 *            The message to print
	 */
	public void println(float s) {
		println(s + "", DEFAULT);
	}

	/**
	 * Prints a int to the log and adds a new line. The default Level is Info
	 * 
	 * @param s
	 *            The message to print
	 */
	public void println(int s) {
		println(s + "", DEFAULT);
	}

	/**
	 * Prints a boolean to the log and adds a new line. The default Level is Info
	 * 
	 * @param s
	 *            The message to print
	 */
	public void println(boolean x) {
		println(x + "", DEFAULT);
	}

	public void println() {
		println("");
	}

	public void println(char x) {
		println(x + "");
	}

	public void println(char[] x) {
		println(new String(x));
	}

	public void println(double x) {
		println("" + x);
	}

	public void println(long x) {
		println("" + x);
	}

	public void println(Object x) {
		println(x.toString());
	}

	public PrintStream append(char c) {
		print(c);
		return this;
	}

	public PrintStream append(CharSequence csq) {
		print(csq);
		return this;
	}

	public PrintStream append(CharSequence csq, int start, int end) {
		print(csq.subSequence(start, end));
		return this;
	}

	/**
	 * Log the message
	 * 
	 * @param s
	 *            The message
	 */
	public void log(String s) {
		println(s);
	}

	/**
	 * Logs the Message with the level warning
	 * 
	 * @param s
	 *            The Message
	 */
	public void warn(String s) {
		println(s, Level.WARNING);
	}

	/**
	 * Logs the Message with the level Error
	 * 
	 * @param s
	 *            The Message
	 */
	public void err(String s) {
		println(s, Level.ERROR);
	}

	/**
	 * This returns the Message the Logger would write
	 * 
	 * @param s
	 *            Your Message, that you want to write out
	 * @param l
	 *            The Level of the Message
	 * @return The Message the Logger would write
	 */
	public static String getMessage(String s, Level l) {
		return getMessage(s, l, (String) null);
	}

	/**
	 * This returns the Message the Logger would write
	 * 
	 * @param s
	 *            Your Message, that you want to write out
	 * @param l
	 *            The Level of the Message
	 * 
	 * @param c
	 *            The class this logger is held by. It will be added in front of the output-level
	 * @return The Message the Logger would write
	 */
	public static String getMessage(String s, Level l, Class<?> c) {
		return getMessage(s, l, c.getSimpleName());
	}

	/**
	 * This returns the Message the Logger would write
	 * 
	 * @param s
	 *            Your Message, that you want to write out
	 * @param l
	 *            The Level of the Message
	 * 
	 * @param className
	 *            The name of the class this logger instanciated. It will be added in front of the output-level
	 * @return The Message the Logger would write
	 */
	public static String getMessage(String s, Level l, String className) {
		String time = (System.currentTimeMillis() / 1000 / 60 / 60 % 24) + (TimeZone.getDefault().inDaylightTime(new Date()) ? 0 : 1) + ":" + System.currentTimeMillis() / 1000 / 60 % 60 + ":" + System.currentTimeMillis() / 1000 % 60;
		s = "[" + time + "]" + (className != null ? "[" + className + "]" : "") + l.getLevelName() + " " + s;
		return s;
	}

	/**
	 * This returns the Message the Logger would write
	 * 
	 * @param s
	 *            Your Message, that you want to write out
	 * @return The Message the Logger would write
	 */
	public static String getMessage(String s) {
		return getMessage(s, DEFAULT);
	}

	/**
	 * Prints the Beginning of a text, all the objects in the middle separated with commas and at the end the end text
	 * 
	 * @param Start
	 *            The Beginning text
	 * @param part
	 *            The Ending text
	 * @param texts
	 *            The Array of objects to print
	 */
	public void println(String Start, String part, Object... texts) {
		String message = Start + texts[0];
		for (int i = 1; i < texts.length; i++) {
			message += part + texts[i];
		}
		println(message);
	}

	public void setLogMethod(Logger log) {
		func = log;
	}
}
