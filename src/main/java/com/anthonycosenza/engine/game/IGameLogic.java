package com.anthonycosenza.engine.game;

import com.anthonycosenza.engine.window.Window;

public interface IGameLogic
{
    void init() throws Exception;
    void input(Window window);
    void update(float interval);
    void render(Window window);
    void cleanup();
    
    Scene getScene();
    
}
