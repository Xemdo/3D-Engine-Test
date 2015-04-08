package us.xemdo.gametest.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SpriteSheet {
	public final String SPRITESHEET_XML_VERSION = "1";
	private BufferedImage spritesheetImg;
	private HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();
	
	public SpriteSheet(String xmlFile) throws SpriteSheetParsingException {
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlFile));
		} catch (SAXException | IOException | ParserConfigurationException ex) {
			throw new SpriteSheetParsingException(ex);
		}
		
		String version, image;
		int padding, spriteWidth, spriteHeight;
		ArrayList<Integer> excludedColors = new ArrayList<Integer>();
				
		for (int i1 = 0; i1 < doc.getChildNodes().getLength(); i1++) {
			// There should only be the spritesheet node at this level.
			Node node1 = doc.getChildNodes().item(i1);
			if (node1.getNodeType() == Node.ELEMENT_NODE && node1.getNodeName().equalsIgnoreCase("spritesheet")) {
				Element elem1 = (Element)node1;
				
				// Check spritesheet version. If there is ever another version in the future this will need backwards compatibility.
				version = elem1.getAttribute("version");
				if (!version.equalsIgnoreCase(SPRITESHEET_XML_VERSION)) {
					throw new SpriteSheetParsingException(xmlFile + ": Mismatching file versions. Excepting \"" + SPRITESHEET_XML_VERSION + "\", read \"" + version + "\" instead.");
				}
				
				// Get attributes
				image = elem1.getAttribute("image");
				padding = getInteger(elem1.getAttribute("padding"), -1);
				spriteWidth = getInteger(elem1.getAttribute("spritewidth"), -1);
				spriteHeight = getInteger(elem1.getAttribute("spriteheight"), -1);
				
				// Check attributes for validity
				if (image == null || padding <= 0 || spriteWidth <= 0 || spriteHeight <= 0) {
					throw new SpriteSheetParsingException(xmlFile + ": Invalid input, negative number, or 0 for one of the following attributes of the spritesheet node: padding, spritewidth, spriteheight");
				}
				
				// Get spritesheet image
				try {
					spritesheetImg = ImageIO.read(new File(new File(xmlFile).getParent() + File.separator + image));
				} catch (IOException ex) {
					throw new SpriteSheetParsingException(ex);
				}
				
				// Go through child nodes
				for (int i2 = 0; i2 < elem1.getChildNodes().getLength(); i2++) {
					Node node2 = elem1.getChildNodes().item(i2);
					if (node2.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					
					Element elem2 = (Element)node2;
					if (elem2.getNodeName().equalsIgnoreCase("excludedcolor")) {
						try {
							excludedColors.add(Color.decode(elem2.getAttribute("hex")).getRGB());
						} catch (NumberFormatException ex) {
							throw new SpriteSheetParsingException(ex);
						}
					} else if (elem2.getNodeName().equalsIgnoreCase("sprite")) {
						String name;
						String[] position;
						int roffset;
						
						name = elem2.getAttribute("name");
						
						if (name == null) {
							throw new SpriteSheetParsingException(xmlFile + ": The name value of at least one sprite is non-existant.");
						}
						
						if (sprites.containsKey(name)) {
							throw new SpriteSheetParsingException(xmlFile + ": Duplicate sprite name: " + name);
						}
						
						if (elem2.getAttribute("pos") == null) {
							throw new SpriteSheetParsingException(xmlFile + ": The position value of the sprite '" + name + "' is non-existant.");
						}
						
						position = elem2.getAttribute("pos").split(",");
						
						if (position.length != 2) {
							throw new SpriteSheetParsingException(xmlFile + ": Invalid position for the sprite named " + name);
						}
						
						if (getInteger(position[0], -1) < 0 || getInteger(position[1], -1) < 0) {
							throw new SpriteSheetParsingException(xmlFile + ": Invalid input or negative number for the position attribute in the sprite named " + name);
						}
						
						if (elem2.getAttribute("roffset") == null) {
							throw new SpriteSheetParsingException(xmlFile + ": The roffset value of the sprite '" + name + "' is non-existant.");
						}
						
						if ((roffset = getInteger(elem2.getAttribute("roffset"), -1)) < 0) {
							throw new SpriteSheetParsingException(xmlFile + ": Invalid input or negative number for the roffset attribute in the sprite named " + name);
						}
						
						sprites.put(name, new Sprite(spritesheetImg.getSubimage(Integer.parseInt(position[0]) * 64 + (Integer.parseInt(position[0]) * 1),
																				Integer.parseInt(position[1]) * 64 + (Integer.parseInt(position[1]) * 1),
																				spriteWidth, spriteHeight), excludedColors, roffset));
					}
				}
			}
		}
	}
	
	public Sprite getSprite(String spriteName) {
		return sprites.get(spriteName);
	}
	
	private int getInteger(String num, int defaultVal) {
		try {
			return Integer.parseInt(num);
		} catch (NumberFormatException ex) {
			return defaultVal;
		}
	}
	
	public static class SpriteSheetParsingException extends Exception {
		private static final long serialVersionUID = -4392954595365184468L;

		public SpriteSheetParsingException(String message) {
			super(message);
		}
		
		public SpriteSheetParsingException(Exception cause) {
			super(cause);
		}
	}
}
