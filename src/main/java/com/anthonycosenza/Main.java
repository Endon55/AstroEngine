package com.anthonycosenza;

import com.anthonycosenza.engine.GameEngine;
import com.anthonycosenza.engine.window.WindowOptions;
import com.anthonycosenza.game.TestGame;

public class Main
{
    public static void main(String[] args)
    {
        GameEngine engine = null;
        try
        {
            engine = new GameEngine("Test Game", new WindowOptions(60, 0, 0), new TestGame());
        } catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        engine.start();
    }
}