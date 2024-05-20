package com.anthonycosenza.engine;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.space.rendering.CanvasRenderer;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.space.Window;
import com.anthonycosenza.engine.events.MessageEvent;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.rendering.projection.Projection2d;
import com.anthonycosenza.engine.space.rendering.Renderer;
import com.anthonycosenza.engine.space.rendering.projection.Projection3d;
import com.anthonycosenza.engine.space.rendering.TextRenderer;
import com.anthonycosenza.engine.util.Constants;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
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
    private boolean vsync = false;
    private Window window;
    private boolean running = true;
    Projection3d projection3d;
    Projection2d projection2d;
    float zNear = .01f;
    float zFar = 1000.0f;
    float fov = 60;
    Renderer renderer;
    Input input;
    Project project;
    
    float physicsUpdatesSecond = 60;
    
    public Engine()
    {
        window = new Window("AstroEngine", 1920, 1080, vsync);
    
        //Essentially turns on OpenGL and allows the window to communicate with it.
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        projection3d = new Projection3d(fov, window.getWidth(), window.getHeight(), zNear, zFar);
        projection2d = new Projection2d(window.getWidth(), window.getHeight());
        
        renderer = new Renderer(window);
        input = new Input(window.getWindowHandle());
        EventBus.getDefault().register(this);
        project = new Project(window.getWidth(), window.getHeight());
        
        glfwSetWindowSizeCallback(window.getWindowHandle(), this::resize);
        
        //Vsync is enabled by default, so we only ever have to disable it.
        if(!vsync)
        {
            glfwSwapInterval(0);
        }
/*        Benchmark benchmark = new Benchmark(10,
                () ->{
            new Texture("resources/images/Ai Sasha.png", true);
        },
                () ->{
                        new Texture("resources/images/Ai Sasha.png", false);
        });
        benchmark.test();*/
        
    }
    
    
    
    
    private boolean handleGuiInput()
    {
        ImGuiIO io = ImGui.getIO();
        io.setMousePos(input.getMousePosition().x(), input.getMousePosition().y());
        io.setMouseDown(0, input.isLeftMouseButtonPressed());
        io.setMouseDown(1, input.isRightMouseButtonPressed());
        
        return io.getWantCaptureMouse() || io.getWantCaptureKeyboard();
    }
    
    private void resize(long windowHandle, int width, int height)
    {
        projection3d.resize(width, height);
        projection2d.resize(width, height);
        renderer.resize(width, height);
    }
    
    private void systemDiagnostics(double fps)
    {
        ImGui.newFrame();
        int windowFlags = 0;
        windowFlags |= ImGuiWindowFlags.NoTitleBar;
        windowFlags |= ImGuiWindowFlags.NoResize;
        ImGui.setNextWindowPos(ImGui.getIO().getDisplaySizeX() - 40, 0, ImGuiCond.FirstUseEver);
        ImGui.begin("Window", windowFlags);
        ImGui.text("" + (((int)(fps * 1000)) / 1000));
        ImGui.end();
        ImGui.endFrame();
        ImGui.render();
    }
    
    public void run()
    {
        running = true;

        //Gives the number of physics updates per second
        float updateInterval = Constants.NANOS_IN_SECOND / physicsUpdatesSecond;
        //Cache this value to avoid repeated calculation.
        float updateTime = updateInterval / Constants.NANOS_IN_SECOND;
        
        long currentTime = System.nanoTime();
        
        double accumulator = 0.0f;
        while(!window.shouldClose() && running)
        {
            input.resetFrame();
            int physicsUpdates = 0;
            long newTime = System.nanoTime();
            long frameTime = newTime - currentTime;
            accumulator += frameTime;
            currentTime = newTime;
            
            //Enables the window to be interacted with by checking for and processing events and summoning the relevant callbacks.
            glfwPollEvents();
            
            //Don't wake up the physics simulation for less this much of a frame
            //We do this so that the physics simulation smooths out compared to the rendering
            while(accumulator >= updateInterval * 1)
            {
                physicsUpdates++;
                //Update physics with an entire timestep
                
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
                    project.physicsUpdate((float)(accumulator / Constants.NANOS_IN_SECOND), input);
                    accumulator = 0;
                }
            }
            double delta = (double) frameTime / Constants.NANOS_IN_SECOND;
            
            handleGuiInput();
            systemDiagnostics(Constants.NANOS_IN_SECOND / (double)frameTime);
            project.uiUpdate(delta, input);
            //Renders the current scene giving it the delta since the last render call.
            
            renderer.render(delta, project.getScene(), projection2d, projection3d);
            
            //Swaps the visible frame buffer for the just compiled frame buffer. Essentially loads the next frame and begins working on the next next frame.
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
        renderer.cleanup();
        window.cleanup();
    }
    
    public void stop()
    {
        running = false;
    }
}
