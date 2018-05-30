package me.steffenjacobs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Steffen Jacobs */
public class FileService {

	private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
	public static final FileService instance = new FileService();

	private void copyFile(File targetDirectory, String file) {
		File f = new File(file);
		File target = new File(targetDirectory.getAbsolutePath(), f.getName());

		try {
			FileUtils.copyFile(f, target);
			LOG.info("Copied file '{}'.", file);
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
		}
	}

	public void handleCopy(String target, List<String> files, String title) {
		// create target
		File targetDirectory = new File(target, title);
		if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
			LOG.error("Could not create target directory '{}'.", targetDirectory);
			return;
		}

		// copy files
		for (String f : files) {
			copyFile(targetDirectory, f);
		}
		LOG.info("Copied {} files.", files.size());
	}
}
