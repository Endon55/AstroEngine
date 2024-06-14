package com.anthonycosenza.engine.ui;

import com.anthonycosenza.engine.loader.Resources;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.flag.ImGuiFreeTypeBuilderFlags;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstroFonts
{
    private static final Map<String, ImFont> fonts = new HashMap<>();
    
    
    /*
     * At some point setup fonts will parse some file to find the fonts it needs and load the correct sizes, for now ImGuiImpl will set the specific fonts.
     */
    public static final String DEFAULT_FONT = "verdana";
    public static void setupFonts()
    {
        setupFonts(List.of(Resources.get("fonts/" + DEFAULT_FONT + ".ttf"),
                Resources.get("fonts/" + DEFAULT_FONT + ".ttf"),
                        Resources.get("fonts/" + DEFAULT_FONT + ".ttf")),
                List.of(16, 24,32));
    }
    public static void setupFonts(List<String> fontPaths, List<Integer> fontSizes)
    {
        if(fontPaths.size() != fontSizes.size()) throw new RuntimeException("Font paths and font sizes do not contain the same number of elements.");
        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImFontConfig fontConfig = new ImFontConfig();
        
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        for(int i = 0; i < fontPaths.size() ; i++)
        {
            ImFont font = fontAtlas.addFontFromFileTTF(fontPaths.get(i), fontSizes.get(i), fontConfig);
            String fontName = new File(fontPaths.get(i)).getName().split("\\.")[0];
            fontName += fontSizes.get(i);
            fonts.put(fontName, font);
        }
        fontConfig.setMergeMode(true);
        fontConfig.destroy();
        
        fontAtlas.setFlags(ImGuiFreeTypeBuilderFlags.LightHinting);
        fontAtlas.build();
    }
    
    public static void push(String fontNameWithSize)
    {
        ImFont font = fonts.get(fontNameWithSize);
        if(font == null) throw new RuntimeException("Can't find font: " + fontNameWithSize);
        ImGui.pushFont(font);
    }
    
    public static void push(String fontName, int fontSize)
    {
        push(fontName + fontSize);
    }
    
    public static void push(int fontSize)
    {
        push(DEFAULT_FONT + fontSize);
    }
    public static void pop()
    {
        ImGui.popFont();
    }
}
