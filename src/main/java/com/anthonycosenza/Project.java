package com.anthonycosenza;

import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.input.Input;

import java.util.ArrayList;
import java.util.List;

public abstract class Project
{
    private final ProjectSettings projectSettings;
    private final String projectName;
    private final List<Scene> scenes;
    private int currentScene;

    
    public Project(String projectName)
    {
        this.projectName = projectName;
        this.projectSettings = new ProjectSettings();
        settings(projectSettings);
        scenes = new ArrayList<>();
        currentScene = 0;
    }
    
    public Project(String projectName, ProjectSettings projectSettings)
    {
        this.projectName = projectName;
        this.projectSettings = projectSettings;
        settings(projectSettings);
        scenes = new ArrayList<>();
        currentScene = 0;
    }
    
    public ProjectSettings getSettings()
    {
        return projectSettings;
    }
    
    public String getProjectName()
    {
        return projectName;
    }
    
    protected void addScene(Scene scene)
    {
        this.scenes.add(scene);
    }
    
    public void setCurrentScene(Scene scene)
    {
        currentScene = scenes.indexOf(scene);
    }
    
    public void setCurrentScene(int sceneIndex)
    {
        if(sceneIndex < 0 || sceneIndex >= scenes.size()) throw new ArrayIndexOutOfBoundsException("Scene index is out of bounds: " + sceneIndex);
        currentScene = sceneIndex;
    }
    public List<Scene> getScenes()
    {
        return scenes;
    }
    
    public int getCurrentScene()
    {
        return currentScene;
    }
    
    public Scene getScene()
    {
        if(scenes.isEmpty())
        {
            return new Scene();
        }
        else return scenes.get(currentScene);
    }
    abstract public void settings(ProjectSettings settings);
    abstract public void initialize(int width, int height);
    
    abstract public void uiUpdate(float delta, Scene scene, Input input);
    abstract public void update(float delta, Scene scene, Input input);
    abstract public void updatePhysics(float delta, Scene scene, Input input);
}
