package me.steffenjacobs.playlistexporter.strategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.playlistexporter.FileService;
import me.steffenjacobs.playlistexporter.domain.asx.Asx;
import me.steffenjacobs.playlistexporter.domain.asx.Asx.Entry;
import me.steffenjacobs.playlistexporter.domain.asx.Asx.Entry.Ref;

/**
 * Strategy for Windows Media Player Playlists (.wpl)
 * 
 * @see <a
 *      href=https://msdn.microsoft.com/en-us/library/dd564688(VS.85).aspx>Microsoft
 *      Documentation of .wpl</a>
 * @author Steffen Jacobs
 */
public class ASXStrategy implements PlaylistFormatStrategy {

	private FileService fileService = FileService.instance;

	private static final Logger LOG = LoggerFactory.getLogger(ASXStrategy.class);

	/**
	 * when exporting a playlist with windows media player, the default file
	 * format for ASX files is ANSI.
	 */
	// TODO: make charset available via start parameter
	private static final String CHARSET_ASX = "Cp1252";

	@Override
	public boolean isFormat(File file) {
		try {
			unmarshal(file);
			return true;
		} catch (JAXBException e) {
			try {
				File file2 = fileService.fixXml(file, CHARSET_ASX);
				unmarshal(file2);
				Files.delete(file2.toPath());
				return true;
			} catch (JAXBException | IOException e2) {
				LOG.error(e2.getLocalizedMessage());
				return false;
			}
		}
	}

	private Asx unmarshal(File file) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Asx.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Asx) jaxbUnmarshaller.unmarshal(file);
	}

	@Override
	public void readPlaylistFile(File file, String target) {
		System.setProperty("user.dir", file.getParent());
		Asx asx;
		try {
			asx = unmarshal(file);
		} catch (JAXBException e) {
			try {
				File file2 = fileService.fixXml(file, CHARSET_ASX);
				asx = unmarshal(file2);
				Files.delete(file2.toPath());
			} catch (JAXBException | IOException e2) {
				LOG.error(e2.getLocalizedMessage());
				return;
			}
		}

		List<String> files = getFiles(asx);
		String title = determineTitle(file, asx);
		fileService.handleCopy(target, files, title);
	}

	/** finds out playlist title or use filename instead */
	private String determineTitle(File file, Asx asx) {
		if (asx == null) {
			return "";
		}
		String title;
		if (asx.getTitle() != null) {
			title = asx.getTitle();
		} else {
			title = FilenameUtils.removeExtension(file.getName());
		}
		title = title.trim();
		return title;
	}

	private List<String> getFiles(Asx asx) {
		if (asx == null) {
			LOG.info("The playlist is empty.");
			return new ArrayList<>();
		}
		LOG.info("Copying files from list {}", asx.getTitle());

		List<Entry> entries = asx.getEntry();
		List<String> result = new ArrayList<>();
		for (Entry e : entries) {
			List<Object> objects = e.getDurationOrParamOrAuthor();
			for (Object obj : objects) {
				if (obj instanceof Ref) {
					result.add(((Ref) obj).getHref());
				}
			}
		}
		return result;
	}
}
