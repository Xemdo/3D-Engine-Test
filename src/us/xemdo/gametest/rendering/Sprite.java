package us.xemdo.gametest.rendering;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class Sprite {
	/**
	 * The size of a pixel.<br />
	 * Eye level is -1.62f on the y axis, so the pixel size was determined by the eye level of a standing human sprite.
	 */
	private final float PIXEL_CONST = 0.038125f;
	public final int textureID;
	public final int roffset;
	private final int imgWidth;
	private final int imgHeight;
	
	public Sprite(BufferedImage image, ArrayList<Integer> excludedColors, int roffset) {
		this.roffset = roffset;
		this.imgWidth = image.getWidth();
		this.imgHeight = image.getHeight();
		
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); // 4 is for RGBA. If it was just RGB it'd be 3.
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				
				if (excludedColors.contains(pixel)) {
					// Completely transparent.
					buffer.put((byte)0);
					buffer.put((byte)0);
					buffer.put((byte)0);
					buffer.put((byte)0);
				} else {
					buffer.put((byte)((pixel >> 16) & 0xFF));	// Red
					buffer.put((byte)((pixel >> 8) & 0xFF));	// Green
					buffer.put((byte)(pixel & 0xFF));			// Blue
					buffer.put((byte)((pixel >> 24) & 0xFF));	// Alpha
				}
			}
		}
		
		buffer.flip();
		
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		// Setup scaling filtering. GL_NEAREST keeps that pixel-y look that makes it look more like Wolfenstien 3D and Doom.
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void render(float x, float y, float z) {
		glBindTexture(GL_TEXTURE_2D, textureID);
		glBegin(GL_QUADS);
			glTexCoord2f(1, 1);
			glVertex3f(x - (roffset * PIXEL_CONST), y, 1);
			glTexCoord2f(1, 0);
			glVertex3f(x - (roffset * PIXEL_CONST), y + (PIXEL_CONST * imgHeight), 1);
			glTexCoord2f(0, 0);
			glVertex3f(x + (PIXEL_CONST * imgWidth) - (roffset * PIXEL_CONST), y + (PIXEL_CONST * imgHeight), 1);
			glTexCoord2f(0, 1);
			glVertex3f(x + (PIXEL_CONST * imgWidth) - (roffset * PIXEL_CONST), y, 1);
		glEnd();
	}
}
