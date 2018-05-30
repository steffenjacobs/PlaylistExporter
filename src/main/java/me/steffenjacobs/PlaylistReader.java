package me.steffenjacobs;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Steffen Jacobs */
public class PlaylistReader {

	private static final Logger LOG = LoggerFactory.getLogger(PlaylistReader.class);

	public static void main(String[] args) throws FileNotFoundException {
		BasicConfigurator.configure();
		WPLStrategy strat = new WPLStrategy();
		if (args.length > 0) {
			for (String s : args) {

				strat.readPlaylistFile(s, new File(s).getParent());
			}
			LOG.info("Read playlist from {} files.", args.length);
		} else {
			LOG.error("Usage: java -jar ./PlaylistReader.jar <Path-To-PlaylistFile1> [<PlaylistFile2>]* ");
		}
	}
}
