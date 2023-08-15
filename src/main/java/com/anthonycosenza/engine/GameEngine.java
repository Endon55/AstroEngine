package com.anthonycosenza.engine;

import com.anthonycosenza.engine.game.IGameLogic;
import com.anthonycosenza.engine.render.Renderer;
import com.anthonycosenza.engine.window.Window;
import com.anthonycosenza.engine.window.WindowOptions;

public class GameEngine// implements Runnable
{
    //private final Thread gameLoopThread;
    private boolean running;
    
    public static final int TARGET_UPS = 30;
    private final int targetFPS = 60;
    
    private final Window window;
    private final IGameLogic gameLogic;
    private final Renderer renderer;
    
    
    
    public GameEngine(String gameTitle, WindowOptions options, IGameLogic gameLogic) throws Exception
    {
        //gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(gameTitle, options);
        this.gameLogic = gameLogic;
        this.renderer = new Renderer();
    }
    
    public void start()
    {
        running = true;
        run();
        //gameLoopThread.start();
    }
    
    private void init() throws Exception
    {
        gameLogic.init();
        renderer.init();
    }
    
    private void gameLoop() throws Exception
    {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / TARGET_UPS;
        float timeR = targetFPS > 0 ? 1000.0f / targetFPS : 0;
        float deltaUpdate = 0;
        float deltaFPS = 0;
        
        long updateTime = initialTime;
        while(running && !window.shouldClose())
        {
            window.pollEvents();
            
             long now = System.currentTimeMillis();
             deltaUpdate += (now - initialTime) / timeU;
             deltaFPS += (now - initialTime) / timeR;
             
             if(targetFPS <= 0 || deltaFPS >= 1)
             {
                 gameLogic.input(window); //Add Scene and time;
             }
            
             if(deltaUpdate >= 1)
             {
                 long diffTimeMillis = now - updateTime;
                 gameLogic.update(diffTimeMillis); //add window and scene
                 updateTime = now;
                 deltaUpdate--;
             }
             
             if(targetFPS <= 0 || deltaFPS >= 1)
             {
                 renderer.render(window, gameLogic.getScene()); //add scene
                 deltaFPS--;
                 window.update();
             }
             initialTime = now;
        }
        cleanup();
    }
    
    public void cleanup()
    {
        gameLogic.cleanup();
        window.cleanup();
        renderer.cleanup();
    }
    
    
    //@Override
    public void run()
    {
        try{
            //Initializes engine components
            init();
            
            //Starts Game Loop
            gameLoop();
        } catch(Exception exception)
        {
            exception.printStackTrace();
        } finally
        {
            cleanup();
        }
    }
}
