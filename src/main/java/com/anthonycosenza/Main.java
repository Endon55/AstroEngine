package com.anthonycosenza;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.window.WindowOptions;
import com.anthonycosenza.game.TestAppLogic;

public class Main
{
    public static void main(String[] args)
    {
        Engine engine = new Engine("Test Game", new WindowOptions(60, 0, 0, false), new TestAppLogic());

        engine.start();
    }
}