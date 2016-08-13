package ch.aiko.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public abstract class BasicLoader {

	protected HashMap<String, String> variables;
	protected String text, path, currentText;
	protected int line;

	public BasicLoader(String path) {
		this.path = path;
		variables = new HashMap<String, String>();
		text = FileUtil.ReadFileInClassPath(path);
		currentText = text;
		
		init();
		
		decode(true);
		currentText = text;
		decode(false);
	}
	
	public abstract void init();

	public boolean decode(boolean prp) {
		boolean prepro = false;
		boolean semi;
		line = 0;
		try {
			while (!currentText.replace(" ", "").replace("\n", "").equalsIgnoreCase("")) {
				++line;
				String code = currentText.split("\n")[0];

				if (!code.startsWith("#")) {
					prepro = false;
					semi = code.contains(";");
					code = code.split(";")[0];
				} else {
					prepro = true;
					semi = false;
					code = code.substring(1);
				}
				currentText = currentText.substring(code.length() + 1 + (prepro ? 1 : 0));

				if (code.replace(" ", "").equalsIgnoreCase("") || code.startsWith("//")) continue;

				String c = code.split(" ")[0];
				String[] args = null;
				if (code.contains("\"")) {
					code = code.substring(c.length() + 1);
					ArrayList<String> arguments = new ArrayList<String>();
					while (code.contains("\"")) {
						int sp = code.indexOf(" ");
						int fi = code.indexOf("\"");

						if (sp < fi && sp != -1) {
							arguments.add(code.split(" ")[0]);
							code = code.substring(arguments.get(arguments.size() - 1).length() + 1);
							continue;
						}

						int si = code.substring(fi + 1).indexOf("\"") + fi + 1;
						arguments.add(code.substring(fi + 1, si));
						code = code.substring(0, fi) + code.substring(si + 1);
					}
					String[] rest = code.split(" ");
					for (int i = 0; i < rest.length; i++)
						arguments.add(rest[i]);
					args = arguments.toArray(new String[arguments.size()]);
				} else args = code.contains(" ") ? code.substring(c.length() + 1).split(" ") : new String[0];
				if (prepro && prp) {
					if (!executePreprocessor(c, args)) return false;
				} else if (!prp) {
					if (!executeCommand(c, args)) return false;
					if (semi) line--;
				}
			}
		} catch (Throwable t) {
			throwError(line, "Unsuspected Error: " + t.toString());
			t.printStackTrace();
		}

		return true;
	}

	public abstract boolean executePreprocessor(String command, String[] args);

	public abstract boolean executeCommand(String command, String[] args);

	public double resolve(String equation) {
		equation = equation.replace(" ", "");

		String varT = " " + equation.replace("+", " + ").replace("-", " - ").replace("*", " * ").replace("/", " / ").replace("%", " % ").replace("^", " ^ ").replace("√", " √ ") + " ";
		for (String s : varT.split(" ")) {
			if (variables.containsKey(s)) {
				varT = varT.replace(" " + s + " ", variables.get(s));
			}
		}
		equation = varT.replace(" ", "");

		HashMap<Integer, Double> brackets = new HashMap<>();
		int curID = 0;

		while (equation.split(Pattern.quote("(")).length > 1 && equation.split(Pattern.quote(")")).length > 1) {
			int to;
			String[] te = equation.split(Pattern.quote("("));
			for (int i = 0; i < te.length; i++) {
				String t = te[i];
				if (t.replace(" ", "").equalsIgnoreCase("")) continue;
				if (((to = t.indexOf(")")) < t.indexOf("(")) || t.indexOf("(") == -1) {
					String modified = t.substring(0, to == -1 ? t.length() : to);
					double val = resolve(replace(modified, brackets));
					int id = curID++;
					brackets.put(id, val);
					equation = equation.replace("(" + t.substring(0, to == -1 ? t.length() : to) + ")", "$" + id);
				}
			}
		}

		equation = equation.replace("(", "").replace(")", "");

		while (equation.contains("^") || equation.contains("√")) {
			int firstIndex = equation.length();
			for (int i = 0; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '^' || c == '√') {
					firstIndex = i;
					break;
				}
			}

			int startFrom = 0;
			for (int i = firstIndex; i > 0; i--) {
				char c = equation.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
					startFrom = i + 1;
					break;
				}
			}

			int secondIndex = equation.length();
			for (int i = firstIndex + 1; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
					secondIndex = i;
					break;
				}
			}
			String ss1 = replace(equation.substring(startFrom, firstIndex), brackets);
			String ss2 = replace(equation.substring(firstIndex + 1, secondIndex), brackets);

			boolean hex1 = false, hex2 = false;

			if (ss1.startsWith("0x")) hex1 = true;
			if (ss2.startsWith("0x")) hex2 = true;

			double s1 = hex1 ? Long.parseLong(ss1.substring(2), 16) : Double.parseDouble(ss1);
			double s2 = hex2 ? Long.parseLong(ss2.substring(2), 16) : Double.parseDouble(ss2);

			if (equation.charAt(firstIndex) == '√') {
				equation = equation.replace(equation.substring(startFrom, secondIndex), Math.pow(s2, 1 / s1) + "");
			} else {
				equation = equation.replace(equation.substring(startFrom, secondIndex), Math.pow(s1, s2) + "");
			}
		}

		while (equation.contains("*") || equation.contains("/") || equation.contains("%")) {
			int fi = equation.length();
			for (int i = 0; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '*' || c == '/' || c == '%') {
					fi = i;
					break;
				}
			}
			int startFrom = 0;
			for (int i = fi - 1; i > 0; i--) {
				char c = equation.charAt(i);
				if (c == '*' || c == '/' || c == '%' || c == '+' || c == '-') {
					startFrom = i + 1;
					break;
				}
			}
			int si = equation.length();
			for (int i = fi + 1; i < equation.length(); i++) {
				char c = equation.charAt(i);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
					si = i;
					break;
				}
			}

			if (startFrom > fi) startFrom = 0;

			String ss1 = replace(equation.substring(startFrom, fi), brackets);
			String ss2 = replace(equation.substring(fi + 1, si), brackets);

			boolean hex1 = false, hex2 = false;

			if (ss1.startsWith("0x")) hex1 = true;
			if (ss2.startsWith("0x")) hex2 = true;

			double s1 = hex1 ? Long.parseLong(ss1.substring(2), 16) : Double.parseDouble(ss1);
			double s2 = hex2 ? Long.parseLong(ss2.substring(2), 16) : Double.parseDouble(ss2);

			double res = 0;

			if (equation.charAt(fi) == '*') res = s1 * s2;
			else if (equation.charAt(fi) == '/') res = s1 / s2;
			else if (equation.charAt(fi) == '%') res = s1 % s2;

			if (res < 0) {
				int id = curID++;
				brackets.put(id, res);
				equation = equation.replace(equation.substring(startFrom, si), "$" + id);
			} else {
				equation = equation.replace(equation.substring(startFrom, si), res + "");
			}
		}

		while (equation.contains("+") || equation.contains("-")) {
			if (equation.startsWith("-")) {
				double s2 = -Double.parseDouble(equation.substring(1).split(Pattern.quote("-"))[0].split(Pattern.quote("+"))[0]);
				int id = curID++;
				equation = "$" + id + equation.substring(equation.substring(1).split(Pattern.quote("-"))[0].split(Pattern.quote("+"))[0].length() + 1);
				brackets.put(id, s2);
			} else {
				int firstIndex = equation.length();
				for (int i = 0; i < equation.length(); i++) {
					char c = equation.charAt(i);
					if (c == '+' || c == '-') {
						firstIndex = i;
						break;
					}
				}

				int secondIndex = equation.length();
				for (int i = firstIndex + 2; i < equation.length(); i++) {
					char c = equation.charAt(i);
					if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '√') {
						secondIndex = i;
						break;
					}
				}

				String ss1 = replace(equation.substring(0, firstIndex), brackets);
				String ss2 = replace(equation.substring(firstIndex + 1, secondIndex), brackets);

				boolean hex1 = false, hex2 = false;

				if (ss1.startsWith("0x")) hex1 = true;
				if (ss2.startsWith("0x")) hex2 = true;

				double s1 = hex1 ? Long.parseLong(ss1.substring(2), 16) : Double.parseDouble(ss1);
				double s2 = hex2 ? Long.parseLong(ss2.substring(2), 16) : Double.parseDouble(ss2);

				if (equation.charAt(firstIndex) == '-') {
					equation = equation.replace(equation.substring(0, secondIndex), (s1 - s2) + "");
				} else {
					equation = equation.replace(equation.substring(0, secondIndex), (s1 + s2) + "");
				}
			}
		}
		equation = replace(equation, brackets);
		if (equation.startsWith("0x")) equation = "" + Long.parseLong(equation.substring(2), 16);
		return Double.parseDouble(equation);
	}

	public String replace(String s, HashMap<Integer, Double> map) {
		for (Entry<Integer, Double> e : map.entrySet()) {
			s = currentText.replace("$" + e.getKey(), e.getValue() + "");
		}
		return s;
	}

	public boolean throwError(int line) {
		return throwError("Error interpreting line: " + line + ": " + text.split("\n")[line]);
	}

	public boolean throwError(int line, String error) {
		return throwError("Error interpreting line: " + line + ": " + (text.split("\n").length > line ? text.split("\n")[line] : "N/A") + "\n\t" + error);
	}

	public boolean throwError(String error) {
		System.err.println(error);
		return false;
	}

}
