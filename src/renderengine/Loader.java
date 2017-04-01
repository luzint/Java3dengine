package renderengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class Loader {
	
	// LISTS ====================================================================
	// All lists here will keep currently loaded elements. All lists will be
	// deleted once the game is being exited. All lists will be deleted when the
	// Loader.cleanUp() method is called.
	// ==========================================================================
	
	/**
	 * A list of integer positions of all Vertex Array Objects (VAO).
	 */
	private List<Integer> vaos = new ArrayList<Integer>();
	
	/**
	 * A list of integer positions of all Vertex Buffer Objects (VBO).
	 */
	private List<Integer> vbos = new ArrayList<Integer>();
	
	/**
	 * A list of all textures loaded into the game during runtime.
	 */
	private List<Integer> textures = new ArrayList<Integer>();
	
	
	/**
	 * The cleanUp method will delete the lists instantiated at the top of this file.
	 * The lists include the VAOS, VBOS, Textures for the current project.
	 */
	public void cleanUp() {
		for(int vao : vaos)
			GL30.glDeleteVertexArrays(vao);
		
		for(int vbo : vbos)
			GL15.glDeleteBuffers(vbo);
		
		for(int texture : textures)
			GL11.glDeleteTextures(texture);
		
	}
	
	/**
	 * Loads a texture using the Slick2D-utils tools.
	 * 
	 * @param fileName The file that is currently being opened.
	 * @return The integer position of the texture in memory
	 */
	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	/**
	 * Creates and fills a Vertex Array Object (VAO) and returns a model that uses the filled data.
	 * A positions Array is used to determine the x , y, z locations of every vertex.
	 * The texturecoords Array will be used to correctly map textures to the object.
	 * The indeces array will say how many Vertex Buffer Object arrays are allowed in the VAO.
	 * 
	 * @param positions An array of x, y, z coordinates to draw each vertex.
	 * @param indeces The number of vertices
	 * @return A new instance of the RawModel class using the vaoID and indeces length.
	 */
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indeces) {
		int vaoID = createVAO();
		bindIndecesBuffer(indeces);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, indeces.length);
	}
	
	// PRIVATE METHODS ==========================================================
	
	/**
	 * Creates a Vertex Buffer Object that will be bound to a specific index of its
	 * VAO owner.
	 * 
	 * @param indeces The number of indexes allowed in the current VAO
	 */
	private void bindIndecesBuffer(int[] indeces) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indeces);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	/**
	 * Creates opengl vertex arrays, and then adds the vertex array to the current list of vaos.
	 * 
	 * VAO diagrams look like:
	 * 
	 *  ----------------------
	 *  |positions			 | 0
	 *  ----------------------
	 *  |Texture Coords		 | 1
	 *  ----------------------
	 * 
	 * VAO's are filled with a array for each vertex.
	 * 
	 * @return The ID for the current vertex Array.
	 */
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	/**
	 * Stores a array of vertices to a vertex array using opengl.
	 * 
	 * @param attributeNumber
	 * @param data
	 */
	private void storeDataInAttributeList(int attributeNumber,int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	
	// BUFFERS ==================================================================
	
	/**
	 * Loads an Array of float values into a opengl buffer.
	 * 
	 * @param data An array of float values to be loaded into a buffer
	 * @return the buffer object of the array of data
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * Loads an Array of integer values into a opengl buffer.
	 * 
	 * @param data An array of integer values to be loaded into a buffer
	 * @return the buffer object of the array of data
	 */
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * Closes the current VAO
	 */
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
}
