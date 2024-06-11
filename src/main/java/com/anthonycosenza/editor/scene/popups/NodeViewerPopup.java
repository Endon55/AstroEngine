package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node._2d.Node2D;
import com.anthonycosenza.engine.space.node._3d.Node3D;
import com.anthonycosenza.engine.util.ClassUtils;
import com.anthonycosenza.engine.util.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class NodeViewerPopup implements Popup
{
    private boolean finished = false;
    private Vector2i popupSize = new Vector2i(450, 650);
    Set<Class<? extends Node>> classes;
    Set<Class<? extends Node2D>> classes2D;
    Set<Class<? extends Node3D>> classes3D;
    Node node;
    
    public NodeViewerPopup()
    {
        classes = new HashSet<>();
        classes2D = new HashSet<>();
        classes3D = new HashSet<>();
        
        Set<Class<? extends Node>> allClasses = ClassUtils.findAllClasses("com.anthonycosenza", Node.class);
        for(Class<? extends Node> clazz : allClasses)
        {
            if(Node3D.class.isAssignableFrom(clazz))
            {
                classes3D.add((Class<? extends Node3D>) clazz);
            }
            else if(Node2D.class.isAssignableFrom(clazz))
            {
                classes2D.add((Class<? extends Node2D>) clazz);
            }
            else
            {
                classes.add(clazz);
            }
        }
    }
    
    
    
    
    @Override
    public void create()
    {
        int frameConfig = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoTitleBar;
    
        ImGui.setNextWindowSize(popupSize.x(), popupSize.y());
        ImGui.setNextWindowPos(ImGui.getMainViewport().getCenterX() - popupSize.x() * .5f, ImGui.getMainViewport()
                .getCenterY() - popupSize.y() * .5f);
    
        if(ImGui.begin("Popup Window", frameConfig))
        {
            if(ImGuiUtils.activeAndEscHit())
            {
                finished = true;
                node = null;
                ImGui.end();
                return;
            }
            if(ImGui.collapsingHeader("Node3D", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(Class<? extends Node> clazz : classes3D)
                {
                    selectable(clazz);
                }
            }
            if(ImGui.collapsingHeader("Node2D", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(Class<? extends Node> clazz : classes2D)
                {
                    selectable(clazz);
                }
            }
            if(ImGui.collapsingHeader("Node", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(Class<? extends Node> clazz : classes)
                {
                    selectable(clazz);
                }
            }
        }
        ImGui.end();
    }
    
    private void selectable(Class<? extends Node> clazz)
    {
        if(ImGui.selectable(clazz.getSimpleName(), false, ImGuiSelectableFlags.AllowDoubleClick))
        {
            finished = true;
            try
            {
                node = clazz.getConstructor().newInstance();
                node.name = "New " + clazz.getSimpleName();
            } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                    InstantiationException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public Node finish()
    {
        return node;
    }
    
}
