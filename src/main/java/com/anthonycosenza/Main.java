package com.anthonycosenza;


import com.anthonycosenza.engine.Engine;
import org.lwjgl.system.Configuration;

public class Main
{
    public static void main(String[] args)
    {
        Configuration.STACK_SIZE.set(512);
        
        
        
        Engine engine = new Engine();
        engine.run();
    }
}