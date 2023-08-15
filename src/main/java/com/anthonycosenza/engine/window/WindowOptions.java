package com.anthonycosenza.engine.window;

import com.anthonycosenza.engine.GameEngine;

public class WindowOptions
{
    public int fps;
    public int ups = GameEngine.TARGET_UPS;
    
    int width;
    int height;
    
    public WindowOptions()
    {
        this.fps = 0;
        this.width = 0;
        this.height = 0;
    }
    public WindowOptions(int targetFPS, int width, int height)
    {
        this.fps = targetFPS;
        this.width = width;
        this.height = height;
    }
}
