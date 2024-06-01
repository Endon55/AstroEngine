package com.anthonycosenza.engine.util;

import com.anthonycosenza.engine.assets.AssetType;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;

import java.io.File;

public class ImGuiUtils
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
    
    public static void error(String error)
    {
        ImGui.pushStyleColor(ImGuiCol.Text, ImColor.rgba(255, 0, 0, 255));
        ImGui.text(error);
        ImGui.popStyleColor();
    }
    public static void createDragAndDropSource(String tooltip, Object payload)
    {
        if(ImGui.beginDragDropSource())
        {
            ImGui.beginTooltip();
            ImGui.setTooltip(tooltip);
            ImGui.setDragDropPayload(payload, ImGuiCond.Always);
            ImGui.endTooltip();
            ImGui.endDragDropSource();
        }
    }
    public static <T> void createDragAndDropTarget(Class<T> targetClass, DragAndDropTarget target)
    {
        if(ImGui.beginDragDropTarget())
        {
            T payload = ImGui.acceptDragDropPayload(targetClass, ImGuiDragDropFlags.AcceptBeforeDelivery);
            if(ImGui.isMouseDragging(0)) //Peek at payload
            {
                target.peek(payload);
            }
            else
            {
                target.accept(payload);
            }
            ImGui.endDragDropTarget();
        }
    }
    private static boolean ddValidAsset = false;
    private static int RED = -167711939;
    private static int GREEN = -16711936;
    public static void createDragAndDropAssetTarget(AssetType assetType, DragAndDropAssetTarget target)
    {
        //System.out.println("R" + ImColor.rgba(1f, 0, 0, 1f));
        //System.out.println(ImColor.rgba(0, 1f, 0, 1f));
        ImGui.pushStyleColor(ImGuiCol.DragDropTarget, (ddValidAsset ? GREEN : RED));
        if(ImGui.beginDragDropTarget())
        {
            Object payload = ImGui.acceptDragDropPayload(File.class, ImGuiDragDropFlags.AcceptBeforeDelivery);
            if(payload instanceof File file && file.getName().endsWith(".a" + assetType.name().toLowerCase()))
            {
                ddValidAsset = true;
                if(!ImGui.isMouseDragging(0)) //Peek at payload
                {
                    target.accept(file);
                }
            }
            else
            {
                ddValidAsset = false;
            }
            
            ImGui.endDragDropTarget();
        }
        ImGui.popStyleColor();
    }
    
    public static boolean createPopupWindow(Vector2i windowSize)
    {
        int frameConfig = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoTitleBar;
    
        ImGui.setNextWindowSize(windowSize.x(), windowSize.y());
        ImGui.setNextWindowPos(ImGui.getMainViewport().getCenterX() - windowSize.x() * .5f, ImGui.getMainViewport()
                .getCenterY() - windowSize.y() * .5f);
    
        return ImGui.begin("Popup Window", frameConfig);
    }
    
}
