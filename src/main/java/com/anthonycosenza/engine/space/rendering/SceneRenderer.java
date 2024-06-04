package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.SceneManager;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.node._3d.Node3D;
import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Renderable;
import com.anthonycosenza.engine.space.rendering.projection.Projection;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;

public class SceneRenderer
{
    private static final Matrix4f DEFAULT_IDENTITY_MATRIX = new Matrix4f().identity();
    
    private Map<Long, List<Map.Entry<Material, Matrix4f>>> pipelineMap;
    
    public SceneRenderer()
    {
        
        //The color that the window frame gets cleared to right before a new frame is rendered.
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    }
    
    public void render(Node scene, Projection projection)
    {
        render(scene, SceneManager.getCamera(), projection);
    }
    
    
    
    
    
    public void render(Node scene, Camera camera, Projection projection)
    {
        //TODO only do this when stuff changes.
        mapScene(scene);
        
        for(Map.Entry<Long, List<Map.Entry<Material, Matrix4f>>> shader : pipelineMap.entrySet())
        {
            ShaderPipeline pipeline = AssetManager.getInstance().getShader(shader.getKey());
            pipeline.bind();
            
            pipeline.getUniforms().setUniform("projectionMatrix", projection.getMatrix());
            pipeline.getUniforms().setUniform("cameraMatrix", camera.getMatrix());
            
            for(Map.Entry<Material, Matrix4f> materialPair : shader.getValue())
            {
                pipeline.getUniforms().setUniform("entityMatrix", materialPair.getValue());
                materialPair.getKey().set(pipeline);
                
                for(Mesh mesh : materialPair.getKey().getMeshes())
                {
                    mesh.bind();
                    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                }
            }
            pipeline.unbind();;
        }
        
    }
    /*
    public void render2(Node scene, Camera camera, Projection projection)
    {
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        
        //shaderPipeline.bind();
        
        //uniforms.setUniform("projectionMatrix", projection.getMatrix());
        //uniforms.setUniform("cameraMatrix", camera.getMatrix());
        //uniforms.setUniform("textureSampler", 0);
    
        Stack<Node> nodes = new Stack<>();
        nodes.add(scene);
    
        while(!nodes.isEmpty())
        {
            Node node = nodes.pop();
            
            defaultPipeline.bind();
            
            if(node instanceof Renderable renderable)
            {
    
                Model model = renderable.getModel();
                if(model == null) continue;
                defaultPipeline.getUniforms().setUniform("entityMatrix", ((Positional) node).getTransformation());
                
                
                for(Material material : renderable.getModel().getMaterials())
                {
                    material.set(defaultPipeline);
                    defaultPipeline.getUniforms().setUniform("projectionMatrix", projection.getMatrix());
                    defaultPipeline.getUniforms().setUniform("cameraMatrix", camera.getMatrix());
                    for(Mesh mesh : material.getMeshes())
                    {
                        mesh.bind();
                        
                        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
            nodes.addAll(node.children);
        }

        //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glBindVertexArray(0);
        defaultPipeline.unbind();
    }*/
    
    private void mapScene(Node scene)
    {
        pipelineMap = new HashMap<>();
        
        Stack<Node> nodes = new Stack<>();
        nodes.add(scene);
        
        while(!nodes.isEmpty())
        {
            Node node = nodes.pop();
            nodes.addAll(node.children);
            
            if(node instanceof Renderable renderable)
            {
                Model model = renderable.getModel();
                
                if(model == null) continue;
                Matrix4f transform = ((Node3D) renderable).getTransformation();
                for(Material material : model.getMaterials())
                {
                    Map.Entry<Material, Matrix4f> materialPair = Map.entry(material, transform);
                    long pipelineID;
                    if(material.getShaderPipeline() == null)
                    {
                        pipelineID = AssetManager.DEFAULT_SHADER;
                    }
                    else pipelineID = material.getShaderPipeline().resourceID;
                    
                    List<Map.Entry<Material, Matrix4f>> materialPairs = pipelineMap.get(pipelineID);
                    if(materialPairs == null)
                    {
                        materialPairs = new ArrayList<>();
                        materialPairs.add(materialPair);
                        pipelineMap.put(pipelineID, materialPairs);
                    }
                    else
                    {
                        materialPairs.add(materialPair);
                    }
                }
            }
        }
    }
}
