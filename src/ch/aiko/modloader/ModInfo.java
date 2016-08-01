package ch.aiko.modloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ModInfo extends PropertyUtil {

	public ModInfo() {
		
	}
	
    public ModInfo(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String entire = "";
        try {
            String line = "";
            while ((line = reader.readLine()) != null) {
                entire += entire.equalsIgnoreCase("") ? line : ("\n" + line);
            }

            reader.close();
        } catch (IOException e) {
        }

        setEntries(entire);
    }

}
