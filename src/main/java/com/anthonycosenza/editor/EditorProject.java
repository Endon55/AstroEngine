package com.anthonycosenza.editor;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;

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
        //ImGui.getIO().setConfigDockingWithShift(true);
        
        userProject.initialize(width, height);
    }
    
    @Override
    public void uiUpdate(float delta, Scene scene, Input input)
    {
        //
        if(!ImGui.begin("Scene Hierarchy"))
        {
            ImGui.end();
            return;
        }
        //These would all be collapsing headers with some kind of listener?
        if(ImGui.collapsingHeader("Scene"))//set as scene name
        {
            ImGui.indent();
            ImGui.bulletText("Child1");
        }
        
        ImGui.end();
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
