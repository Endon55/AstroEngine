package com.anthonycosenza;

import com.anthonycosenza.rending.Renderer;
import com.anthonycosenza.shader.ShaderData;
import com.anthonycosenza.shader.ShaderPipeline;
import com.anthonycosenza.shape.Pyramid3;
import com.anthonycosenza.transformation.Projection;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Engine
{
    private Window window;
    private boolean running = true;
    Projection projection;
    float zNear = .01f;
    float zFar = 1000.0f;
    float fov = 60;
    Scene scene;
    Entity triangle;
    Renderer renderer;
    
    public Engine()
    {
        window = new Window("GameEngine", 1920, 1080, false);
    
        //Essentially turns on OpenGL and allows the window to communicate with it.
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        projection = new Projection(fov, window.getWidth(), window.getHeight(), zNear, zFar);
        
        scene = new Scene();
        renderer = new Renderer();
    }
    
    
    public void run()
    {
        running = true;
        
        Model model = new Model(new Pyramid3(50, 30));
        Entity entity = model.createEntity();
        entity.setPosition(0, 0, -100);
        scene.addEntity(entity);
        
        float rotation = 0;
        
    
        while(!window.shouldClose() && running)
        {
            rotation += .1f;
            for(Entity entity1 : scene.getEntities())
            {
                entity1.rotate(1, 1, 1, rotation % 360);
            }
            

            renderer.render(scene, projection);
            
            window.update();
        }
    
        cleanup();
    }
    

    
    public void cleanup()
    {
        window.cleanup();
    }
    
    public void stop()
    {
        running = false;
    }
}
