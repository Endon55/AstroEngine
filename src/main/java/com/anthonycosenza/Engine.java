package com.anthonycosenza;

import com.anthonycosenza.events.MessageEvent;
import com.anthonycosenza.input.Input;
import com.anthonycosenza.input.Key;
import com.anthonycosenza.input.KeyAction;
import com.anthonycosenza.rendering.Renderer;
import com.anthonycosenza.shape.Pyramid3;
import com.anthonycosenza.transformation.Projection;
import com.anthonycosenza.util.Constants;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
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
    Renderer renderer;
    Input input;
    Project project;
    
    double physicsUpdatesSecond = 60;
    
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
        project = new Project();
    }
    
    
    public void run()
    {
        running = true;

        //Gives the number of physics updates per second
        double updateInterval = Constants.NANOS_IN_SECOND / physicsUpdatesSecond;
        //Cache this value to avoid repeated calculation.
        double updateTime = updateInterval / Constants.NANOS_IN_SECOND;
        
        long currentTime = System.nanoTime();
        
        double accumulator = 0.0f;
        while(!window.shouldClose() && running)
        {
            int physicsUpdates = 0;
            long newTime = System.nanoTime();
            long frameTime = newTime - currentTime;
            accumulator += frameTime;
            currentTime = newTime;
            
            //Enables the window to be interacted with by checking for and processing events and summoning the relevant callbacks.
            glfwPollEvents();
            
            //Don't wake up the physics simulation for less this much of a frame
            //We do this so that the physics simulation smooths out compared to the rendering
            
            while(accumulator >= updateInterval * .6)
            {
                physicsUpdates++;
                //Update physics with an entire timestep
                //project.physicsUpdate(updateTime, input);
                //accumulator -= updateInterval;
                
                if(accumulator >= updateInterval)
                {
                    //Update physics with an entire timestep
                    //Delta is the number of seconds to advance the physics simulation.
                    project.physicsUpdate(updateTime, input);
                    accumulator -= updateInterval;
                }
                else
                {
                    //Update the physics with a fractional component. Accumulator should be a decimal value less than 1 and we want to get how many nanos that is
                    project.physicsUpdate(accumulator / Constants.NANOS_IN_SECOND, input);
                    accumulator = 0;
                }
            }
            //Renders the current scene giving it the delta since the last render call.
            project.render((double)frameTime / Constants.NANOS_IN_SECOND, renderer, projection);
    
            //Swaps the visible frame buffer for the just compiled frame buffer. Essentially loads the next frame and begins loading of the next next frame.
            glfwSwapBuffers(window.getWindowHandle());
        }
    
        cleanup();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        //System.out.println(event.message);
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
