package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.render.light.AmbientLight;
import com.anthonycosenza.engine.render.light.Attenuation;
import com.anthonycosenza.engine.render.light.DirectionalLight;
import com.anthonycosenza.engine.render.light.PointLight;
import com.anthonycosenza.engine.render.light.SceneLighting;
import com.anthonycosenza.engine.render.light.SpotLight;
import com.anthonycosenza.engine.render.light.shadow.CascadeShadow;
import com.anthonycosenza.engine.render.model.Material;
import com.anthonycosenza.engine.render.model.Mesh;
import com.anthonycosenza.engine.render.model.Model;
import com.anthonycosenza.engine.render.model.Texture;
import com.anthonycosenza.engine.render.model.animation.AnimationData;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Fog;
import com.anthonycosenza.engine.scene.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
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
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("viewMatrix");
        uniformsMap.createUniform("bonesMatrices");
        uniformsMap.createUniform("textureSampler");
        uniformsMap.createUniform("normalSampler");
        uniformsMap.createUniform("material.ambient");
        uniformsMap.createUniform("material.diffuse");
        uniformsMap.createUniform("material.specular");
        uniformsMap.createUniform("material.reflectance");
        uniformsMap.createUniform("material.hasNormalMap");
        uniformsMap.createUniform("ambientLight.factor");
        uniformsMap.createUniform("ambientLight.color");
        
        //We have to create pseudo arrays with name strings, so they can be accessed in the shaders.
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
        
        uniformsMap.createUniform("fog.activeFog");
        uniformsMap.createUniform("fog.color");
        uniformsMap.createUniform("fog.density");
    
        for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++)
        {
            uniformsMap.createUniform("shadowMap[" + i + "]");
            uniformsMap.createUniform("cascadeShadows[" + i + "].projViewMatrix");
            uniformsMap.createUniform("cascadeShadows[" + i + "].splitDistance");
        }
    }
    
    private void updateLights(Scene scene)
    {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
        SceneLighting sceneLighting = scene.getSceneLighting();
        AmbientLight ambientLight = sceneLighting.getAmbientLight();
        DirectionalLight directionalLight = sceneLighting.getDirectionalLight();
        
        uniformsMap.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniformsMap.setUniform("ambientLight.color", ambientLight.getColor());
    
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
    
    
    public void render(Scene scene, ShadowRenderer shadowRenderer)
    {
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        shaderProgram.bind();
        
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        updateLights(scene);
        
        Fog fog = scene.getFog();
        uniformsMap.setUniform("fog.activeFog", fog.isActive() ? 1 : 0);
        uniformsMap.setUniform("fog.color", fog.getColor());
        uniformsMap.setUniform("fog.density", fog.getDensity());
        
        uniformsMap.setUniform("textureSampler", 0);
        uniformsMap.setUniform("normalSampler", 1);
        
        int start = 2;
        List<CascadeShadow> cascadeShadows = shadowRenderer.getCascadeShadows();
        for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++)
        {
            uniformsMap.setUniform("shadowMap[" + i + "]", start + i);
            CascadeShadow cascadeShadow = cascadeShadows.get(i);
            uniformsMap.setUniform("cascadeShadows[" + i + "].projViewMatrix", cascadeShadow.getProjViewMatrix());
            uniformsMap.setUniform("cascadeShadows[" + i + "].splitDistance", cascadeShadow.getSplitDistance());
        }
        
        shadowRenderer.getShadowBuffer().bindTextures(GL_TEXTURE2);
        
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
                String normalMapPath = material.getNormalMapPath();
                boolean hasNormalMapPath = normalMapPath != null;
                uniformsMap.setUniform("material.hasNormalMap", hasNormalMapPath ? 1 : 0);
                Texture texture = textureCache.getTexture(material.getTexturePath());
                //Bind the default texture to slot 0
                glActiveTexture(GL_TEXTURE0);
                texture.bind();
                
                if(hasNormalMapPath)
                {
                    Texture normalMapTexture = textureCache.getTexture(normalMapPath);
                    //Bind the normal map to slot 1
                    glActiveTexture(GL_TEXTURE1);
                    normalMapTexture.bind();
                }
                
                for(Mesh mesh : material.getMeshList())
                {
                    glBindVertexArray(mesh.getVaoID());
                    for(Entity entity : entities)
                    {
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        AnimationData animationData = entity.getAnimationData();
                        if(animationData == null)
                        {
                            uniformsMap.setUniform("bonesMatrices", AnimationData.DEFAULT_BONES_MATRICES);
                        }
                        else
                        {
                            uniformsMap.setUniform("bonesMatrices", animationData.getCurrentFrame().boneMatrices());
                        }
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
