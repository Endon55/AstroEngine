package com.anthonycosenza.editor;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiDockNodeState;

public class EditorProject extends Project
{
    Project userProject;
    public EditorProject(Project userProject, ProjectSettings editorSettings)
    {
        super(userProject.getProjectName(), editorSettings);
        this.userProject = userProject;
    }

    
    @Override
    public void initialize(int width, int height)
    {
        ImGuiIO io = ImGui.getIO();
        userProject.initialize(width, height);
        io.setIniFilename(Editor.getEditorIniFile() + ".ini");
        io.setWantSaveIniSettings(true);
        //ImGui.saveIniSettingsToDisk(Editor.getEditorIniFile() + ".ini");
    }
    
    @Override
    public void uiUpdate(float delta, Scene scene, Input input)
    {
        int frameConfig = 0;
        
      
        //Sets the whole window as a docking area. Doesn't require any sizing, it auto fills the whole dang deal, roger doger.
        
        int dockspaceConfig = ImGuiDockNodeFlags.PassthruCentralNode;
        int mainDock = ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), dockspaceConfig);
      
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        if(ImGui.begin("Scene Hierarchy", frameConfig))
        {
            if(ImGui.collapsingHeader("Scene"))//set as scene name
            {
                ImGui.indent();
                ImGui.bulletText("Child1");
            }
        }
        ImGui.end();
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        if(ImGui.begin("Scene Hierarchy2", frameConfig))
        {
            if(ImGui.collapsingHeader("Scene2"))//set as scene name
            {
                ImGui.indent();
                ImGui.bulletText("Child1");
            }
        }
        ImGui.end();
    
    
        //ImGui.saveIniSettingsToDisk("settings");
        
        /*
        if(ImGui.begin(userProject.getProjectName(), frameConfig))
        {
            if(ImGui.beginMenuBar())
            {
                if(ImGui.beginMenu("File"))
                {
                    if(ImGui.menuItem("Open"))
                    {
                        //System.out.println("Open");
                    }
                    //ImGui.menuItem("Save");
                    //ImGui.menuItem("Save As");
                    
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
            ImGui.setNextWindowPos(0, 20);
            ImGui.setNextWindowSize(getSettings().width / 2, getSettings().height / 2);
            if(ImGui.begin("Scene Hierarchy", 0))
            {
                if(ImGui.collapsingHeader("Scene"))//set as scene name
                {
                    ImGui.indent();
                    ImGui.bulletText("Child1");
                }
            }
            ImGui.end();
        }
        ImGui.end();*/


    }
    
    @Override
    public void update(float delta, Scene scene, Input input)
    {
        //userProject.update(delta, scene, input);
    }
    
    @Override
    public void updatePhysics(float delta, Scene scene, Input input)
    {
        //userProject.update(delta, scene, input);
    }
    
    
    @Override
    public void setCurrentScene(Scene scene)
    {
        userProject.setCurrentScene(scene);
    }
    
    @Override
    public void setCurrentScene(int sceneIndex)
    {
        userProject.setCurrentScene(sceneIndex);
    }
    
    @Override
    public Scene getScene()
    {
        return userProject.getScene();
    }
    
    /*
     * This isn't necessary since we passed the settings directly.
     */
    @Override
    public void settings(ProjectSettings settings)
    { }
}
