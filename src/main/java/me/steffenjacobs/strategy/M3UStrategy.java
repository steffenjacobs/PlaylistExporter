package me.steffenjacobs.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.FileService;

/** @author Steffen Jacobs */
public class M3UStrategy implements PlaylistFormatStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(M3UStrategy.class);	
	private static final String CHARSET_M3U = "Cp1252";

	private FileService fileService = FileService.instance;

	@Override
	public boolean isFormat(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			return line.startsWith("#EXTM3U");
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void readPlaylistFile(File file, final String target) {
		System.setProperty("user.dir", file.getParent());
		final String title = FilenameUtils.removeExtension(file.getName());
		try {
			List<String> files = Files.lines(file.toPath(), Charset.forName(CHARSET_M3U)).filter(line -> !line.startsWith("#") && !line.isEmpty()).collect(Collectors.toList());
			fileService.handleCopy(target, files, title);
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
		}
	}
}
