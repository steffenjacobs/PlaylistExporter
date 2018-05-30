package me.steffenjacobs.strategy;

import java.io.File;

/** @author Steffen Jacobs */
public interface PlaylistFormatStrategy {

	public void readPlaylistFile(File file, String target);

}
