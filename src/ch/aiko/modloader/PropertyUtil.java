package ch.aiko.modloader;

import java.util.HashMap;

public class PropertyUtil {

    private final HashMap<String, String> entries = new HashMap<>();
    private final HashMap<String, String> comments = new HashMap<>();

    public PropertyUtil(String keyvaluetext) {
        setEntries(keyvaluetext);
    }

    public PropertyUtil() {
    }

    public final void setEntries(String keyvaluetext) {
        entries.clear();
        comments.clear();

        String[] lines = keyvaluetext.split("\n");
        for (String line : lines) {
            boolean comment = false;
            if (line.startsWith("#")) {
                comment = true;
            }

            while (line.startsWith(" ")) {
                line = line.substring(1);
            }

            if (line.contains("=")) {
                String key = line.split("=")[0];
                String value = line.substring(key.length() + 1);
                key = key.replace(" ", "");
                if (comment) {
                    comments.put(key, value);
                } else {
                    entries.put(key, value);
                }
            }
        }
    }

    public String get(String key) {
        return entries.containsKey(key) ? entries.get(key) : key;
    }

    public String getComment(String key) {
        return comments.containsKey(key) ? comments.get(key) : key;
    }

}
