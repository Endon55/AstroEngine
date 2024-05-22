package com.anthonycosenza;

import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.input.Input;

public abstract class Project
{
    private final ProjectSettings projectSettings;
    private final String projectName;
    Scene scene;

    
    public Project(String projectName)
    {
        this.projectName = projectName;
        this.projectSettings = new ProjectSettings();
        settings(projectSettings);
    }
    
    public ProjectSettings getSettings()
    {
        return projectSettings;
    }
    
    public String getProjectName()
    {
        return projectName;
    }
    
    public Scene getScene()
    {
        return scene;
    }
    abstract public void settings(ProjectSettings settings);
    abstract public void initialize(int width, int height);
    abstract public void uiUpdate(double delta, Input input);
    abstract public void update(float delta, Input input);
    abstract public void updatePhysics(float delta, Input input);
}
