package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.editor.scripts.ScriptCompiler;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node._2d.Node2D;
import com.anthonycosenza.engine.space.node._3d.Node3D;
import com.anthonycosenza.engine.util.ClassUtils;
import com.anthonycosenza.engine.util.FileUtils;
import com.anthonycosenza.engine.util.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2i;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeViewerPopup implements Popup
{
    private boolean finished = false;
    private Vector2i popupSize = new Vector2i(450, 650);
    
    private static Set<Class<? extends Node>> engineClasses1D;
    private static Set<Class<? extends Node2D>> engineClasses2D;
    private static Set<Class<? extends Node3D>> engineClasses3D;
    private Node node;
    private List<File> scriptFiles;
    
    
    public NodeViewerPopup()
    {
        scriptFiles = EditorIO.getAllProjectScripts();
            //Scripts need to be recompiled to make sure we have the most up to date versions of everything
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
            if(!scriptFiles.isEmpty() && ImGui.collapsingHeader("User Classes", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(File script : scriptFiles)
                {
                    selectable(script);
                }
            }
            if(ImGui.collapsingHeader("Node3D", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(Class<? extends Node> clazz : getEngineClasses3D())
                {
                    selectable(clazz);
                }
            }
            if(ImGui.collapsingHeader("Node2D", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(Class<? extends Node> clazz : getEngineClasses2D())
                {
                    selectable(clazz);
                }
            }
            if(ImGui.collapsingHeader("Node", ImGuiTreeNodeFlags.DefaultOpen))
            {
                for(Class<? extends Node> clazz : getEngineClasses())
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
                node.setName("New " + clazz.getSimpleName());
            } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                    InstantiationException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void selectable(File script)
    {
        if(ImGui.selectable(FileUtils.getFileName(script), false, ImGuiSelectableFlags.AllowDoubleClick))
        {
            finished = true;
    
            ScriptCompiler.compile();
            Class<? extends Node> clazz = (Class <? extends Node>)ScriptCompiler.load(FileUtils.getFileName(script));

            try
            {
                node = clazz.getConstructor().newInstance();
                node.setName("New " + clazz.getSimpleName());
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
    
    
    
    
    
    
    public static Set<Class<? extends Node3D>> getEngineClasses3D()
    {
        if(engineClasses3D == null) createEngineClasses();
        return engineClasses3D;
    }
    
    public static Set<Class<? extends Node2D>> getEngineClasses2D()
    {
        if(engineClasses2D == null) createEngineClasses();
        return engineClasses2D;
    }
    
    public static Set<Class<? extends Node>> getEngineClasses()
    {
        if(engineClasses1D == null) createEngineClasses();
        return engineClasses1D;
    }
    
    private static void createEngineClasses()
    {
        Set<Class<? extends Node>> allClasses = ClassUtils.findAllClasses("com.anthonycosenza", Node.class);
        engineClasses1D = new HashSet<>();
        engineClasses2D = new HashSet<>();
        engineClasses3D = new HashSet<>();
        for(Class<? extends Node> clazz : allClasses)
        {
            if(Node3D.class.isAssignableFrom(clazz))
            {
                engineClasses3D.add((Class<? extends Node3D>) clazz);
            }
            else if(Node2D.class.isAssignableFrom(clazz))
            {
                engineClasses2D.add((Class<? extends Node2D>) clazz);
            }
            else
            {
                engineClasses1D.add(clazz);
            }
        }
    }
    
}
