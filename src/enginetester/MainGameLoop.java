package enginetester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objconverter.ModelData;
import objconverter.OBJFileLoader;
import renderengine.DisplayManager;
import renderengine.Loader;
import renderengine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		ModelData data;
		
		List<Entity> entities = new ArrayList<Entity>();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		//Creates Tree model
		data = OBJFileLoader.loadOBJ("tree");
		RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel treeTexturedModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
		
		//creates fern model
		data = OBJFileLoader.loadOBJ("fern");
		RawModel fernModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel fernTexturedModel = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));
		
		//creates grassModel
		data = OBJFileLoader.loadOBJ("grassModel");
		RawModel grassModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel grassTexturedModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		
		//creates flower
		RawModel flowerModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel flowerTexturedModel = new TexturedModel(flowerModel, new ModelTexture(loader.loadTexture("flower")));
		
		
		Random random = new Random();
		for(int i = 0; i < 500; i++) {
			entities.add(new Entity(treeTexturedModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 5));
		}
		for(int i = 0; i < 300; i++) {
			entities.add(new Entity(fernTexturedModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 1));
		}
		for(int i = 0; i < 400; i++) {
			entities.add(new Entity(grassTexturedModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 1));
		}
		for(int i = 0; i < 100; i++) {
			entities.add(new Entity(flowerTexturedModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 1));
		}
		
		Light light = new Light(new Vector3f(3000,2000,2000), new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0, -1, loader,  texturePack, blendMap);
		Terrain terrain1 = new Terrain(-1, -1, loader,  texturePack, blendMap);
	
		data = OBJFileLoader.loadOBJ("stanfordBunny");
		RawModel bunny = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel bunnyTextured = new TexturedModel(bunny, new ModelTexture(loader.loadTexture("white")));
		
		Player player = new Player(bunnyTextured, new Vector3f(0, 0, -50), 1, 0, 0, 5);
		
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()) {
			camera.move();
			player.move();
			
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain1);
			for(Entity entity : entities) {
				renderer.processEntity(entity);
			}
			renderer.processEntity(player);
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
