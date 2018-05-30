package me.steffenjacobs.strategy;

import java.io.File;
import java.util.Arrays;

/** @author Steffen Jacobs */
public interface PlaylistFormatStrategy {

	public void readPlaylistFile(File file, String target);
	
	public boolean isFormat(File file);
	
	public static PlaylistFormatStrategy determineStrategy(File file) {
		for (PlaylistFormatStrategy s : Arrays.asList(new WPLStrategy(), new M3UStrategy())) {
			if (s.isFormat(file)) {
				return s;
			}
		}
		return null;
	}
}
