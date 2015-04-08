package us.xemdo.gametest.rendering.screen;

import static org.lwjgl.opengl.GL11.*;
import us.xemdo.gametest.rendering.PerspectiveCamera;
import us.xemdo.gametest.rendering.SpriteSheet;
import us.xemdo.gametest.rendering.SpriteSheet.SpriteSheetParsingException;

public class GameScreen implements Screen {
	private PerspectiveCamera camera;
	private SpriteSheet testSpriteSheet;
	
	public GameScreen() {
		try {
			testSpriteSheet = new SpriteSheet("res/sprites/test.xml");
		} catch (SpriteSheetParsingException ex) {
			// This'll be gone eventually
			ex.printStackTrace();
			System.exit(1);
		}
		
		this.camera = new PerspectiveCamera(0, -1.62f, -3);
	}
	
	@Override
	public void render() {
		testSpriteSheet.getSprite("standing-north").render(0, 0, 0);
		
		camera.update();
		glLoadIdentity();
		camera.lookThrough();
	}
}
