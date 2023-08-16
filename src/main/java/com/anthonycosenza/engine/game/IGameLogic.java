package com.anthonycosenza.engine.game;

import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;

public interface IGameLogic
{
    void init(Window window) throws Exception;
    void input(Window window);
    void update(float interval);
    void render(Window window);
    void cleanup();
    
    Scene getScene();
    
}
