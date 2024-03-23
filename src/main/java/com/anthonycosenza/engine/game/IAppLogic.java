package com.anthonycosenza.engine.game;

import com.anthonycosenza.engine.render.Render;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;

public interface IAppLogic
{
    void init(Window window, Scene scene, Render render) throws Exception;
    void input(Window window, Scene scene, long difTimeMillis);
    void update(Window window, Scene scene, float interval);
    void cleanup();
    
    
}
