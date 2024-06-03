package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.SceneManager;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.space.rendering.materials.Texture;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Positional;
import com.anthonycosenza.engine.space.node.Renderable;
import com.anthonycosenza.engine.space.rendering.projection.Projection;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import org.joml.Matrix4f;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SceneRenderer
{
    private static final Matrix4f DEFAULT_IDENTITY_MATRIX = new Matrix4f().identity();
    
    
    private final ShaderPipeline shaderPipeline;
    private final UniformMap uniforms;
    
    public SceneRenderer()
    {
        shaderPipeline = new ShaderPipeline(new ShaderData("AstroEngine/resources/shaders/scene.vert", GL_VERTEX_SHADER),
                new ShaderData("AstroEngine/resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
        
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("cameraMatrix");
        uniforms.createUniform("entityMatrix");
        uniforms.createUniform("hasTexture");
        uniforms.createUniform("textureSampler");
        uniforms.createUniform("material.diffuse");
        
        //The color that the window frame gets cleared to right before a new frame is rendered.
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        

    }
    
    public void render(Node scene, Projection projection)
    {
        render(scene, SceneManager.getCamera(), projection);
    }
    
    public void render(Node scene, Camera camera, Projection projection)
    {
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        
        shaderPipeline.bind();
        
        uniforms.setUniform("projectionMatrix", projection.getMatrix());
        uniforms.setUniform("cameraMatrix", camera.getMatrix());
        uniforms.setUniform("textureSampler", 0);
    
        Stack<Node> nodes = new Stack<>();
        nodes.add(scene);
    
        while(!nodes.isEmpty())
        {
            Node node = nodes.pop();
            
            
            if(node instanceof Renderable renderable)
            {
                uniforms.setUniform("entityMatrix", ((Positional) node).getTransformation());
                Model model = renderable.getModel();
                if(model == null) continue;
                for(StandardMaterial material : renderable.getModel().getMaterials())
                {
                    uniforms.setUniform("material.diffuse", material.getDiffuseColor());
                    Texture texture = material.getTexture();
                    glActiveTexture(GL_TEXTURE0);
                    if(texture != null)
                    {
                        uniforms.setUniform("hasTexture", 1);
                        texture.bind();
                    }
                    else uniforms.setUniform("hasTexture", 0);
                    
                    
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
        shaderPipeline.unbind();
    }
}
