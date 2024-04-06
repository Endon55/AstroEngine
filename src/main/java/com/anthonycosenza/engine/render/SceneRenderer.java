package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.render.light.AmbientLight;
import com.anthonycosenza.engine.render.light.Attenuation;
import com.anthonycosenza.engine.render.light.DirectionalLight;
import com.anthonycosenza.engine.render.light.PointLight;
import com.anthonycosenza.engine.render.light.SceneLighting;
import com.anthonycosenza.engine.render.light.SpotLight;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * SceneRenderer handles the data handoff between Java code and OpenGL, and is responsible for what is seen on screen.
 */
public class SceneRenderer
{
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    
    
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    
    public SceneRenderer()
    {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);

        createUniforms();
    }
    
    private void createUniforms()
    {
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("viewMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("textureSampler");
        uniformsMap.createUniform("material.ambient");
        uniformsMap.createUniform("material.diffuse");
        uniformsMap.createUniform("material.specular");
        uniformsMap.createUniform("material.reflectance");
        uniformsMap.createUniform("ambientLight.factor");
        uniformsMap.createUniform("ambientLight.color");
    
        for(int i = 0; i < MAX_POINT_LIGHTS; i++)
        {
            String name = "pointLights[" + i + "]";
            uniformsMap.createUniform(name + ".position");
            uniformsMap.createUniform(name + ".color");
            uniformsMap.createUniform(name + ".intensity");
            uniformsMap.createUniform(name + ".attenuation.constant");
            uniformsMap.createUniform(name + ".attenuation.linear");
            uniformsMap.createUniform(name + ".attenuation.exponent");
        }
        for(int i = 0; i < MAX_SPOT_LIGHTS; i++)
        {
            String name = "spotLights[" + i + "]";
            uniformsMap.createUniform(name + ".pointLight.position");
            uniformsMap.createUniform(name + ".pointLight.color");
            uniformsMap.createUniform(name + ".pointLight.intensity");
            uniformsMap.createUniform(name + ".pointLight.attenuation.constant");
            uniformsMap.createUniform(name + ".pointLight.attenuation.linear");
            uniformsMap.createUniform(name + ".pointLight.attenuation.exponent");
            uniformsMap.createUniform(name + ".coneDirection");
            uniformsMap.createUniform(name + ".cutoff");
        }
    
        uniformsMap.createUniform("directionalLight.color");
        uniformsMap.createUniform("directionalLight.direction");
        uniformsMap.createUniform("directionalLight.intensity");
        
    }
    
    private void updateLights(Scene scene)
    {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
        SceneLighting sceneLighting = scene.getSceneLighting();
        
        AmbientLight ambientLight = sceneLighting.getAmbientLight();
        uniformsMap.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniformsMap.setUniform("ambientLight.color", ambientLight.getColor());
    
        DirectionalLight directionalLight = sceneLighting.getDirectionalLight();
        Vector4f auxDirection = new Vector4f(directionalLight.getDirection(), 0);
        auxDirection.mul(viewMatrix);
        Vector3f direction = new Vector3f(auxDirection.x, auxDirection.y, auxDirection.z);
        uniformsMap.setUniform("directionalLight.color", directionalLight.getColor());
        uniformsMap.setUniform("directionalLight.direction", direction);
        uniformsMap.setUniform("directionalLight.intensity", directionalLight.getIntensity());
        
        List<PointLight> pointLights = sceneLighting.getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;
        for(int i = 0; i < MAX_POINT_LIGHTS; i++)
        {
            if(i < numPointLights)
            {
                pointLight = pointLights.get(i);
            }
            else pointLight = null;
            String name = "pointLights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix);
        }
        List<SpotLight> spotLights = sceneLighting.getSpotLights();
        int numSpotLights = spotLights.size();
        SpotLight spotLight;
        for(int i = 0; i < MAX_SPOT_LIGHTS; i++)
        {
            if(i < numSpotLights)
            {
                spotLight = spotLights.get(i);
            }
            else spotLight = null;
            String name = "spotLights[" + i + "]";
            updateSpotLight(spotLight, name, viewMatrix);
    
        }
    }
    
    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix)
    {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if(pointLight != null)
        {
            aux.set(pointLight.getPosition(), 1);
            aux.mul(viewMatrix);
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();
            Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }
        uniformsMap.setUniform(prefix + ".position", lightPosition);
        uniformsMap.setUniform(prefix + ".color", color);
        uniformsMap.setUniform(prefix + ".intensity", intensity);
        uniformsMap.setUniform(prefix + ".attenuation.constant", constant);
        uniformsMap.setUniform(prefix + ".attenuation.linear", linear);
        uniformsMap.setUniform(prefix + ".attenuation.exponent", exponent);
    }
    
    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix)
    {
        PointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if(spotLight != null)
        {
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutOff();
            pointLight = spotLight.getPointLight();
        }
        
        uniformsMap.setUniform(prefix + ".coneDirection", coneDirection);
        uniformsMap.setUniform(prefix + ".cutoff", cutoff);
        updatePointLight(pointLight, prefix + ".pointLight", viewMatrix);
    }
    
    
    public void render(Scene scene)
    {
        shaderProgram.bind();
        
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
        
        uniformsMap.setUniform("textureSampler", 0);
        
        updateLights(scene);
        
        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for(Model model : models)
        {
            List<Entity> entities = model.getEntityList();
            for(Material material : model.getMaterialList())
            {
                uniformsMap.setUniform("material.ambient", material.getDiffuseColor());
                uniformsMap.setUniform("material.diffuse", material.getDiffuseColor());
                uniformsMap.setUniform("material.specular", material.getDiffuseColor());
                uniformsMap.setUniform("material.reflectance", material.getDiffuseColor());
                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE);
                texture.bind();
                for(Mesh mesh : material.getMeshList())
                {
                    glBindVertexArray(mesh.getVaoID());
                    for(Entity entity : entities)
                    {
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        
        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public void cleanup()
    {
        if(shaderProgram != null)
        {
            shaderProgram.cleanup();
        }
    }
}
