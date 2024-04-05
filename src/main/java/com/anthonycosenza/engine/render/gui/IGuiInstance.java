package com.anthonycosenza.engine.render.gui;

import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;

public interface IGuiInstance
{
    void drawGui();
    boolean handleGuiInput(Scene scene, Window window);
}
