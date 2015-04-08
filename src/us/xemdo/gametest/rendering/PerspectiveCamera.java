package us.xemdo.gametest.rendering;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * A 3D OpenGL camera. Also handles input from the keyboard and mouse.
 * @author Xemdo
 *
 */
public class PerspectiveCamera {
	private Vector3f position;
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	
	private float movementSpeed = 0.002f;
	private float mouseSensitivity = 0.15f;
	float dx, dy;
	
	public PerspectiveCamera(float x, float y, float z) {
		position = new Vector3f(x, y, z);
	}
	
	public void update() {
		handleMouse();
		handleKeyboard();
	}
	
	public void yaw(float amount) {
		yaw += amount;
		
		if (yaw >= 360)
			yaw -= 360;
		else if (yaw <= -360)
			yaw += 360;
	}
	
	public void pitch(float amount) {
		if (pitch + amount > 90) {
			pitch = 90;
			return;
		} else if (pitch + amount < -90) {
			pitch = -90;
			return;
		}
		
		pitch += amount;
	}
	
	public void walkForward(float distance) {
		position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
		position.z += distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void walkBackwards(float distance) {
		position.x += distance * (float)Math.sin(Math.toRadians(yaw));
		position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void strafeLeft(float distance) {
		position.x -= distance * (float)Math.sin(Math.toRadians(yaw - 90));
		position.z += distance * (float)Math.cos(Math.toRadians(yaw - 90));
	}
	
	public void strafeRight(float distance) {
		position.x -= distance * (float)Math.sin(Math.toRadians(yaw + 90));
		position.z += distance * (float)Math.cos(Math.toRadians(yaw + 90));
	}
	
	public void floatUp(float distance) {
		position.y -= distance;
	}
	
	public void floatDown(float distance) {
		position.y += distance;
	}
	
	public void lookThrough() {
		glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		glTranslatef(position.x, position.y, position.z);
	}
	
	public void handleMouse() {
		dx = Mouse.getDX();
		dy = Mouse.getDY();
		
		yaw(dx * mouseSensitivity);
		pitch(-dy * mouseSensitivity);
	}
	
	public void handleKeyboard() {
		// TODO: Replace the static keys with private variables that can be changed through settings
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			System.exit(0);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			walkForward(movementSpeed);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			walkBackwards(movementSpeed);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			strafeLeft(movementSpeed);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			strafeRight(movementSpeed);
		}
		
		// Everything below is only for testing purposes and should eventually be removed
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			floatUp(movementSpeed);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			floatDown(movementSpeed);
		}
	}
}
