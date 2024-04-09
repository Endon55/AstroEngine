package com.anthonycosenza.engine;

import com.anthonycosenza.engine.game.IAppLogic;
import com.anthonycosenza.engine.render.Render;
import com.anthonycosenza.engine.render.gui.IGuiInstance;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;
import com.anthonycosenza.engine.window.WindowOptions;

public class Engine
{
    public static final int TARGET_UPS = 30;
    private final IAppLogic logic;
    private final Window window;
    private Render renderer;
    private boolean running;
    private Scene scene;
    
    private int targetFPS;
    private int targetUPS;
    
    public Engine(String windowTitle, WindowOptions options, IAppLogic logic)
    {
        window = new Window(windowTitle, options, ()->
        {
            resize();
            return null;
        });
        targetFPS = options.fps;
        targetUPS = options.ups;
        this.logic = logic;
        this.renderer = new Render(window);
        scene = new Scene(window.getWidth(), window.getHeight());
        logic.init(window, scene, renderer);
        running = true;
    }
    private void resize()
    {
        int width = window.getWidth();
        int height = window.getHeight();
        scene.resize(width, height);
        renderer.resize(width, height);
    }

    private void run()
    {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / targetUPS;
        float timeR = targetFPS > 0 ? 1000.0f / targetFPS : 0;
        float deltaUpdate = 0;
        float deltaFPS = 0;
        
        long updateTime = initialTime;
        IGuiInstance iGuiInstance = scene.getGuiInstance();
        while(running && !window.shouldClose())
        {
            window.pollEvents();
            
             long now = System.currentTimeMillis();
             deltaUpdate += (now - initialTime) / timeU;
             deltaFPS += (now - initialTime) / timeR;
             
             if(targetFPS <= 0 || deltaFPS >= 1)
             {
                 window.getMouseInput().input();
                 boolean inputConsumed = iGuiInstance != null ? iGuiInstance.handleGuiInput(scene, window) : false;
                 logic.input(window, scene, now - initialTime, inputConsumed); //Add Scene and time;
             }
            
             if(deltaUpdate >= 1)
             {
                 long diffTimeMillis = now - updateTime;
                 logic.update(window, scene, diffTimeMillis); //add window and scene
                 updateTime = now;
                 deltaUpdate--;
             }
             
             if(targetFPS <= 0 || deltaFPS >= 1)
             {
                 renderer.render(window, scene); //add scene
                 deltaFPS--;
                 window.update();
             }
             initialTime = now;
        }
        
        cleanup();
    }
    
    public void cleanup()
    {
        logic.cleanup();
        renderer.cleanup();
        scene.cleanup();
        window.cleanup();
    }
    
    public void start()
    {
        running = true;
        run();
    }
    public void stop()
    {
        running = false;
    }
}
