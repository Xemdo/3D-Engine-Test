package us.xemdo.gametest;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import us.xemdo.gametest.rendering.screen.GameScreen;
import us.xemdo.gametest.rendering.screen.Screen;

public class Engine {
	public static void main(String args[]) {
		new Engine().run();
	}
	
	private long lastFPS;
	private int fps;
	private Screen currentScreen;
	
	private void run() {
		init();
		loop();
	}
	
	private void init() {
		try {
			Display.setDisplayMode(new DisplayMode(1024, 768));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		
		GLU.gluPerspective(45.0f, ((float)1024 / (float)768), 0.1f, 100.0f);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		glClearColor(0.0627f, 0.4705f, 0.6235f, 0f);
		glClearDepth(1.0f);
		
		Display.setTitle("3D Engine Test");
		lastFPS = System.currentTimeMillis();
		Mouse.setGrabbed(true);
		
		currentScreen = new GameScreen();
	}
	
	private void loop() {
		while (!Display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
						
			currentScreen.render();
			
			Display.update();
			updateFPS();
		}
		
		Display.destroy();
	}
	
	private void updateFPS() {
		if (System.currentTimeMillis() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS = System.currentTimeMillis();
		} else {
			fps++;
		}
	}
}
