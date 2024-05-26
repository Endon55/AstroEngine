package com.anthonycosenza.engine;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.Window;
import com.anthonycosenza.engine.events.MessageEvent;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.rendering.Renderer;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.space.rendering.projection.Projection;
import com.anthonycosenza.engine.ui.ImGuiImpl;
import com.anthonycosenza.engine.util.Constants;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

public class Engine
{
    private final Project project;
    private final ProjectSettings settings;
    private final Window window;
    private boolean running = true;
    private final Projection projection;
    private float zNear = .01f;
    private float zFar = 3000.0f;
    private float fov = 60;
    private final Renderer renderer;
    private final Input input;
    private float physicsUpdatesSecond = 60;
    private ImGuiImpl imGuiImpl;
    
    
    /*public Engine(Project project, Window window)
    {
    
    }*/
    
    
    public Engine(Project project)
    {
        this.project = project;
        this.settings = project.getSettings();
        
        /*
         * LWJGL Config settings.
         */
        Configuration.STACK_SIZE.set(settings.lwjglStackSize);
    
    
        
        window = new Window(project.getProjectName(), settings);
    
        //Essentially turns on OpenGL and allows the window to communicate with it.
        GL.createCapabilities();
        input = new Input(window.getWindowHandle(), this.settings);
    
        //Initialize ImGui
        imGuiImpl = new ImGuiImpl(window, settings);
        
        
        projection = new Projection(fov, window.getWidth(), window.getHeight(), zNear, zFar);
        renderer = new Renderer(window);
        
        EventBus.getDefault().register(this);
        this.project.initialize(window.getWidth(), window.getHeight());
        
        //Window Resize callback
        glfwSetWindowSizeCallback(window.getWindowHandle(), this::resize);
        
    
        //Keeps errors from being saved as long files.
        GLFWErrorCallback.createPrint(System.err).set();
        
        //Vsync is enabled by default, so we only ever have to disable it.
        if(!settings.vsync)
        {
            glfwSwapInterval(0);
        }
        
        glEnable(GL_DEPTH_TEST);
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
            int physicsUpdates = 0;
            long newTime = System.nanoTime();
            long frameTime = newTime - currentTime;
            accumulator += frameTime;
            currentTime = newTime;
            
            //Enables the window to be interacted with by checking for and processing events and summoning the relevant callbacks.
            glfwPollEvents();
    
    
            imGuiImpl.newFrame();
            input.resetFrame();
            
            Scene scene = project.getScene();
            
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
                    project.updatePhysics(updateTime, scene, input);
                    accumulator -= updateInterval;
                }
                else
                {
                    //Update the physics with a fractional component. Accumulator should be a decimal value less than 1 and we want to get how many nanos that is
                    project.updatePhysics((float)(accumulator / Constants.NANOS_IN_SECOND), scene, input);
                    accumulator = 0;
                }
            }
            float delta = (float) frameTime / Constants.NANOS_IN_SECOND;
            
            handleGuiInput();
            
            if(settings.enableSystemDiagnostics)
            {
                systemDiagnostics(Constants.NANOS_IN_SECOND / (double) frameTime);
            }
            
            project.uiUpdate(delta, scene, input);
            

            project.update(delta, scene, input);
            //Renders the current scene giving it the delta since the last render call.
    
            ImGui.endFrame();
            ImGui.render();
            
            renderer.render(delta, project.getScene(), projection);
            //Swaps the visible frame buffer for the just compiled frame buffer. Essentially loads the next frame and begins working on the next next frame.
            if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable))
            {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }
            //imGuiImpl.endFrame();
            glfwSwapBuffers(window.getWindowHandle());
        }
    
        cleanup();
    }
    
    private boolean handleGuiInput()
    {
        ImGuiIO io = ImGui.getIO();
        io.setMousePos(input.getMousePosition().x(), input.getMousePosition().y());
        io.setMouseDown(0, input.isLeftMouseButtonPressed());
        io.setMouseDown(1, input.isRightMouseButtonPressed());
        
        return io.getWantCaptureMouse() || io.getWantCaptureKeyboard();
    }
    
    
    private void systemDiagnostics(double fps)
    {
        int windowFlags = 0;
        windowFlags |= ImGuiWindowFlags.NoTitleBar;
        windowFlags |= ImGuiWindowFlags.NoResize;
        //ImGui.setNextWindowPos(window.getRightEdge() - 70, window.getTopEdge(), ImGuiCond.FirstUseEver);
    
       // ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 70, ImGui.getMainViewport().getPosY() + 15, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(0, 0, ImGuiCond.FirstUseEver);
        ImGui.begin("Window", windowFlags);
        ImGui.text("" + (((int) (fps * 1000)) / 1000));
        ImGui.end();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        //System.out.println(event.message);
    }
    
    
    private void resize(long windowHandle, int width, int height)
    {
        window.resize(width, height);
        
        projection.resize(width, height);
        renderer.resize(width, height);
        
        settings.width = width;
        settings.height = height;
        ImGui.getIO().setDisplaySize(width, height);
    }
    
    public void cleanup()
    {
        renderer.cleanup();
        window.cleanup();
        imGuiImpl.clean();;
    }
    
    public void stop()
    {
        running = false;
    }
}
