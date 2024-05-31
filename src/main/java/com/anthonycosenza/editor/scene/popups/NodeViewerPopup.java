package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node._2d.Node2D;
import com.anthonycosenza.engine.space.node._3d.Node3D;
import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeViewerPopup implements Popup
{
    private boolean finished = false;
    private Vector2i popupSize = new Vector2i(450, 650);
    private Reflections reflections;
    Set<Class<? extends Node>> classes;
    Set<Class<? extends Node2D>> classes2D;
    Set<Class<? extends Node3D>> classes3D;
    Node node;
    
    public NodeViewerPopup()
    {
        reflections = new Reflections(Node.class, Scanners.SubTypes);
        classes = reflections.getSubTypesOf(Node.class);
        classes2D = reflections.getSubTypesOf(Node2D.class);
        classes2D.add(Node2D.class);
        classes3D = reflections.getSubTypesOf(Node3D.class);
        classes3D.add(Node3D.class);
        classes = classes.stream().filter(aClass -> !classes2D.contains(aClass) && !classes3D.contains(aClass)).collect(Collectors.toSet());
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
