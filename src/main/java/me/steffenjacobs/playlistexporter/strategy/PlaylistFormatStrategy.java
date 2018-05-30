package me.steffenjacobs.playlistexporter.strategy;

import java.io.File;
import java.util.Arrays;

/** @author Steffen Jacobs */
public interface PlaylistFormatStrategy {

	/**
	 * reads the <i>file</i> with the playlist and copies all the files
	 * referenced in the playlist into the <i>target</i>-directory
	 */
	public void readPlaylistFile(File file, String target);

	/**
	 * @return true: if this strategy is the appropriate strategy to handle the
	 *         <i>file</i>
	 */
	public boolean isFormat(File file);

	/** @return the correct strategy to handle the <i>file</i> or null */
	public static PlaylistFormatStrategy determineStrategy(File file) {
		for (PlaylistFormatStrategy s : Arrays.asList(new WPLStrategy(), new M3UStrategy(), new ASXStrategy())) {
			if (s.isFormat(file)) {
				return s;
			}
		}
		return null;
	}
}
