package renderengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

public class MasterRenderer {

	// ALL OF THESE VARIABLES SHOULD BE PLACED IN A RESOURCES FOLDER AND CHANGABLE ===
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private static final float RED = 0.5f;

	private static final float GREEN = 0.5f;
	private static final float BLUE = 0.5f;
	private static final float FOG_DENSITY = 0.0007f;

	private static final float FOG_GRADIENT = 0.5f;
	// DOWN TO HERE. --------------------------------------------------------------

	private Matrix4f projectionMatrix;
	
	
	// LOADS ALL SHADERS  ===========================================================
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	// ------------------------------------------------------------------------------

	// HOLDS A MAP OF ALL ACTIVE RENDERABLE OBJECTS ================================
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	// -----------------------------------------------------------------------------

	/**
	 * No args() Constructor
	 */
	public MasterRenderer() {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}

	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	/**
	 * Will allow both sides of a surface to be rendered
	 */
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * Makes only the outside facing vertex visible
	 */
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BACK);
	}


	/**
	 * Clears the window for the next frame. To be called every iteration of the
	 * game loop.
	 */
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}

	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);

		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);

		}
	}

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void render(Light sun, Camera camera) {
		prepare();
		shader.start();
		shader.loadFog(FOG_DENSITY, FOG_GRADIENT);
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();

		terrainShader.start();
		terrainShader.loadLight(sun);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadFog(FOG_DENSITY, FOG_GRADIENT);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		
		
		terrains.clear();
		entities.clear();
	}
}
