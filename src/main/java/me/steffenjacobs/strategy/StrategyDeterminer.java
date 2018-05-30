package me.steffenjacobs.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Steffen Jacobs */
public class StrategyDeterminer {

	private static final Logger LOG = LoggerFactory.getLogger(StrategyDeterminer.class);

	public PlaylistFormatStrategy determineStrategy(File file) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			reader.close();
			if (line.startsWith("#EXTM3U")) {
				return new M3UStrategy();
			}
		} catch (FileNotFoundException e) {
			LOG.error(e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
		}
		return new WPLStrategy();
	}
}
