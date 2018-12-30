package de.akoeck.simple.blockchain.client;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.springframework.stereotype.Component;

@Component
public class SimpleBlockchainSettings {

	private final static String NODE = "de/akoeck/simple/blockchain/client";

	private final static String LAST_SAVE_PATH_KEY = "lastSavePath";
	private Preferences prefs = Preferences.userRoot().node(NODE);

	public String getLastSaveDirectory() {
		return prefs.get(LAST_SAVE_PATH_KEY, System.getProperty("user.home"));
	}
	
	public void setLastSaveDirectory(final String path) {
		prefs.put(LAST_SAVE_PATH_KEY, path);
	}

	public void save() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
