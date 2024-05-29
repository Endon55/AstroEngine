package com.anthonycosenza.engine.ui;

import imgui.ImGui;

public class UITools
{
    private static float spaceWidth = -1;
    
    /*
     * This is a pretty amusing way to do this. I insert spaces at the front until it's approximately centered.
     */
    public static String centerAlignOffset(String text, float fieldWidth)
    {
        float stringWidth = ImGui.calcTextSize(text).x;
        float indentation = (fieldWidth - stringWidth) * .5f;
        int spaces = (int) (indentation / getSpaceWidth());
        StringBuilder textBuilder = new StringBuilder(text);
        for(int i = 0; i < spaces; i++)
        {
            textBuilder.insert(0, " ");
        }
        return textBuilder.toString();
    }
    public static float getSpaceWidth()
    {
        if(spaceWidth == -1)
        {
            spaceWidth = ImGui.calcTextSize(" ").x;
        }
        return spaceWidth;
    }
}
