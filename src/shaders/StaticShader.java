package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColour;
	private int location_fogDensity;
	private int location_fogGradient;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");	
		location_skyColour = super.getUniformLocation("skyColour");
		location_fogDensity = super.getUniformLocation("fogDensity");
		location_fogGradient = super.getUniformLocation("fogGradient");
	}
	
	public void loadFog(float fogDensity, float fogGradient) {
		super.loadFloat(location_fogDensity, fogDensity);
		super.loadFloat(location_fogGradient, fogGradient);
	}
	
	public void loadSkyColour(float r, float g, float b) {
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadFakeLightingVariable(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	public void loadShineVariables(float damper, float ref) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, ref);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLight(Light light) {
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
