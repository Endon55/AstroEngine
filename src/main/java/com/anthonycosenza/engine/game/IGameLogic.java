package com.anthonycosenza.engine.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;

public interface IGameLogic
{
    void init(Window window) throws Exception;
    void input(Window window, Scene scene, long difTimeMillis);
    void update(Window window, Scene scene, float interval);
    void render(Window window);
    void cleanup();
    
    Scene getScene();
    
}
