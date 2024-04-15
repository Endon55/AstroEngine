package com.anthonycosenza;

import com.anthonycosenza.events.MessageEvent;
import com.anthonycosenza.input.Input;
import com.anthonycosenza.input.Key;
import com.anthonycosenza.input.KeyAction;
import com.anthonycosenza.rendering.Renderer;
import com.anthonycosenza.shape.Pyramid3;
import com.anthonycosenza.transformation.Projection;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
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
    Input input;
    
    public Engine()
    {
        window = new Window("AstroEngine", 1920, 1080, false);
    
        //Essentially turns on OpenGL and allows the window to communicate with it.
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        projection = new Projection(fov, window.getWidth(), window.getHeight(), zNear, zFar);
        
        scene = new Scene();
        renderer = new Renderer();
        input = new Input(window.getWindowHandle());
        EventBus.getDefault().register(this);
    }
    
    
    public void run()
    {
        running = true;
        
        Model model = new Model(new Pyramid3(50, 30));
        Entity entity = model.createEntity();
        entity.setPosition(0, 0, -100);
        scene.addEntity(entity);
        scene.getCamera().setRotationDeg(0, 0, 0);
        float rotation = 0;
        float moveSpeed = .1f;
    
        while(!window.shouldClose() && running)
        {
            rotation += .01f;
           /* for(Entity entity1 : scene.getEntities())
            {
                entity1.rotate(1, 1, 1, rotation % 360);
            }*/
            //scene.getCamera().setRotationDeg(0, rotation, 0);
            System.out.println(input.getState(Key.A));
            if(input.getState(Key.A) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
            {
                scene.getCamera().moveLocalX(-moveSpeed);
            }
            if(input.getState(Key.D) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
            {
                scene.getCamera().moveLocalX(moveSpeed);
            }
            if(input.getState(Key.W) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
            {
                scene.getCamera().moveLocalZ(moveSpeed);
            }
            if(input.getState(Key.S) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
            {
                scene.getCamera().moveLocalZ(-moveSpeed);
            }
            
            
            //scene.getCamera().moveLocalZ(-rotation);

            renderer.render(scene, projection);
            
            window.update();
        }
    
        cleanup();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        System.out.println(event.message);
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
