package me.steffenjacobs.strategy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.FileService;
import me.steffenjacobs.domain.asx.Asx;
import me.steffenjacobs.domain.asx.Asx.Entry;
import me.steffenjacobs.domain.asx.Asx.Entry.Ref;

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
	private static final String CHARSET_ASX = "Cp1252";

	@Override
	public boolean isFormat(File file) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Asx.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.unmarshal(file);
			return true;
		} catch (JAXBException e) {
			try {
				File file2 = fixXml(file);
				JAXBContext jaxbContext = JAXBContext.newInstance(Asx.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				jaxbUnmarshaller.unmarshal(file2);
				Files.delete(file2.toPath());
				return true;
			} catch (JAXBException | IOException e2) {
				LOG.error(e2.getLocalizedMessage());
				return false;
			}
		}
	}

	@Override
	public void readPlaylistFile(File file, String target) {
		System.setProperty("user.dir", file.getParent());
		Asx asx;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Asx.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			asx = (Asx) jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			try {
				File file2 = fixXml(file);
				JAXBContext jaxbContext = JAXBContext.newInstance(Asx.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				asx = (Asx) jaxbUnmarshaller.unmarshal(file2);
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

	private File fixXml(File file) {
		String str;
		File file2 = new File(file.getParent(), System.currentTimeMillis() + "-temp");
		try {
			file2.createNewFile();
			str = FileUtils.readFileToString(file, CHARSET_ASX);
			str = str.replaceAll("&[^a]", "&amp; ");
			FileUtils.writeStringToFile(file2, str, Charset.forName("UTF-8"));
			return file2;
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage());
		}
		return file2;
	}

	/** finds out playlist title or use filename instead */
	private String determineTitle(File file, Asx asx) {
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
		if (asx != null) {
			LOG.info("Copying files from list {}", asx.getTitle());
		}

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
