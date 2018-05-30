package me.steffenjacobs.playlistexporter;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.playlistexporter.strategy.PlaylistFormatStrategy;

/** @author Steffen Jacobs */
public class PlaylistExporter {

	private static final Logger LOG = LoggerFactory.getLogger(PlaylistExporter.class);

	public static void main(String[] args) {
		BasicConfigurator.configure();
		if (args.length > 0) {
			for (String s : args) {
				File f = new File(s);
				if (!f.exists()) {
					LOG.error("File '{}' not found.", f.getAbsolutePath());
					return;
				}
				PlaylistFormatStrategy strat = PlaylistFormatStrategy.determineStrategy(f);
				if (strat == null) {
					LOG.error("Playlist format for file '{}' is not supported. Supported formats are: WPL, M3U, ASX.", f.getAbsolutePath());
					return;
				}
				strat.readPlaylistFile(f, f.getParent());
			}
			LOG.info("Read playlist from {} files.", args.length);
		} else {
			LOG.error("Usage: java -jar ./PlaylistExporter.jar <Path-To-PlaylistFile1> [<PlaylistFile2>]* ");
		}
	}
}
