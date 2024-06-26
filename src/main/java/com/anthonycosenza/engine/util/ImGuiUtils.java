package com.anthonycosenza.engine.util;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetInfo;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.AssetType;
import com.anthonycosenza.engine.input.Key;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
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
    
    public static String padRight(String text, float fieldWidth)
    {
        float stringWidth = ImGui.calcTextSize(text).x;
        float indentation = (fieldWidth - stringWidth);
        int spaces = (int) (indentation / getSpaceWidth());
        StringBuilder textBuilder = new StringBuilder(text);
        textBuilder.append(" ".repeat(spaces));
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

    private static boolean ddValidAsset = false;
    private static int RED = ImColor.rgba(255, 0, 0, 255);
    private static int GREEN = ImColor.rgba(0, 255, 0, 255);
    public static Asset assetDragAndDropTarget(AssetType assetType)
    {
        final Asset[] asset = {null};
        ImGuiUtils.createDragAndDropAssetTarget(assetType,
                file ->
                {
                    AssetInfo info = Toml.getAssetHeader(file);
                    asset[0] = AssetManager.getInstance().getAsset(info.assetID());
                
                });
        return asset[0];
    }
    
    public static File createDragAndDropTarget(DragAndDropFilter filter)
    {
        //System.out.println("R" + ImColor.rgba(1f, 0, 0, 1f));
        //System.out.println(ImColor.rgba(0, 1f, 0, 1f));
        ImGui.pushStyleColor(ImGuiCol.DragDropTarget, (ddValidAsset ? GREEN : RED));
        File out = null;
        if(ImGui.beginDragDropTarget())
        {
            File payload = ImGui.acceptDragDropPayload(File.class, ImGuiDragDropFlags.AcceptBeforeDelivery);
            if(payload != null)
            {
                if(filter.filter(payload))
                {
                    ddValidAsset = true;
                    if(!ImGui.isMouseDragging(0))
                    {
                        out = payload;
                    }
                }
                else
                {
                    ddValidAsset = false;
                }
            }
            ImGui.endDragDropTarget();
        }
        ImGui.popStyleColor();
        return out;
    }
    public static void createDragAndDropAssetTarget(AssetType assetType, DragAndDropAssetTarget target)
    {
        //System.out.println("R" + ImColor.rgba(1f, 0, 0, 1f));
        //System.out.println(ImColor.rgba(0, 1f, 0, 1f));
        ImGui.pushStyleColor(ImGuiCol.DragDropTarget, (ddValidAsset ? GREEN : RED));
        if(ImGui.beginDragDropTarget())
        {
            Object payload = ImGui.acceptDragDropPayload(File.class, ImGuiDragDropFlags.AcceptBeforeDelivery);
            if(payload instanceof File file && file.getName().endsWith(assetType.getExtension()))
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
    
    public static boolean activeAndEscHit()
    {
        return ImGui.isWindowFocused() && Engine.INPUT.isPressed(Key.ESCAPE);
    }
    
    public static float getScrollPercentX()
    {
        return ImGui.getScrollX() / ImGui.getScrollMaxX();
    }
    
    public static float getScrollPercentY()
    {
        return ImGui.getScrollY() / ImGui.getScrollMaxY();
    }
    
    
    public static void resizeTextBuffer(ImString string, int newSize)
    {
        int oldSize = string.getBufferSize();
        if(newSize > oldSize)
        {
            string.resize(newSize);
        }
    }
    
    
    public interface DragAndDropFilter
    {
        boolean filter(File payload);
    }
    
    public interface DragAndDropTarget
    {
        void peek(File payload);
    
        void accept(File payload);
    }
    
}
