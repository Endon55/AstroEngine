package com.anthonycosenza;


import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.loader.text.Font;

public class Main
{
    public static void main(String[] args)
    {
        //Font font = new Font("resources/fonts/Bagnard.otf");
        
        //System.out.println(font.getFontData().unitsPerEm);
        Engine engine = new Engine();
        engine.run();
    }
}