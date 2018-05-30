package me.steffenjacobs.strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.FileService;
import me.steffenjacobs.domain.wpl.Smil;
import me.steffenjacobs.domain.wpl.Smil.Body.Seq.Media;

/**
 * Strategy for Windows Media Player Playlists (.wpl)
 * 
 * @see <a
 *      href=https://msdn.microsoft.com/en-us/library/dd564688(VS.85).aspx>Microsoft
 *      Documentation of .wpl</a>
 * @author Steffen Jacobs
 */
public class WPLStrategy implements PlaylistFormatStrategy {

	private FileService fileService = FileService.instance;

	private static final Logger LOG = LoggerFactory.getLogger(WPLStrategy.class);

	@Override
	public boolean isFormat(File file) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Smil.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.unmarshal(file);
			return true;
		} catch (JAXBException e) {
			return false;
		}
	}

	@Override
	public void readPlaylistFile(File file, String target) {
		System.setProperty("user.dir", file.getParent());
		Smil smil;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Smil.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			smil = (Smil) jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			LOG.error(e.getLocalizedMessage());
			return;
		}

		List<String> files = getFiles(smil);
		String title = determineTitle(file, smil);
		fileService.handleCopy(target, files, title);
	}

	/** finds out playlist title or use filename instead */
	private String determineTitle(File file, Smil smil) {
		String title;
		if (smil.getHead() != null && smil.getHead().getTitle() != null) {
			title = smil.getHead().getTitle();
		} else {
			title = FilenameUtils.removeExtension(file.getName());
		}
		return title;
	}

	private List<String> getFiles(Smil smil) {
		if (smil != null && smil.getHead() != null) {
			LOG.info("Copying files from list {} by {}", smil.getHead().getTitle(), smil.getHead().getAuthor());
		}

		if (smil != null && smil.getBody() == null || smil.getBody().getSeq() == null || smil.getBody().getSeq().getMedia() == null) {
			if (smil.getHead() == null) {
				LOG.error("Invalid playlist");
				return new ArrayList<>();
			}
			LOG.error("Invalid playlist: '{}'", smil.getHead().getTitle());
			return new ArrayList<>();
		}
		List<Media> media = smil.getBody().getSeq().getMedia();
		return media.stream().map(Media::getSrc).collect(Collectors.toList());
	}
}
