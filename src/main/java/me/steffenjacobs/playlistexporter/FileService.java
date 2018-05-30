package me.steffenjacobs.playlistexporter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
		if (!f.exists()) {
			try {
				f = new File(f.getCanonicalPath());
			} catch (IOException e) {
				LOG.error(e.getLocalizedMessage());
				return;
			}
		}
		File target = new File(targetDirectory.getAbsolutePath(), f.getName());

		try {
			FileUtils.copyFile(f, target);
			LOG.info("Copied file '{}'.", file);
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
		}
	}

	/**
	 * copies a list of <i>files</i> into a new folder with the name
	 * <i>title</i> in the <i>target</i>-directory
	 */
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

	/**
	 * fixes the XML (relevant for ASX files exported with the Windows Media
	 * Player: WMP does not standard-conform XML
	 */
	public File fixXml(File file, String charset) {
		String str;
		File file2 = new File(file.getParent(), System.currentTimeMillis() + "-temp");
		try {
			if (!file2.createNewFile()) {
				throw new IOException(String.format("Could not create temporary file '%s'", file2.getAbsolutePath()));
			}
			str = FileUtils.readFileToString(file, charset);
			str = str.replaceAll("&[^a]", "&amp; ");
			FileUtils.writeStringToFile(file2, str, Charset.forName("UTF-8"));
			return file2;
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
		}
		return file2;
	}
}
