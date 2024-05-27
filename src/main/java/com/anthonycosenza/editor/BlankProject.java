package com.anthonycosenza.editor;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;

public class BlankProject extends Project
{
    public BlankProject()
    {
        super("Empty");
    }
    
    @Override
    public void settings(ProjectSettings settings)
    {
    
    }
    
    @Override
    public void initialize(int width, int height)
    {
    
    }
    
    @Override
    public void uiUpdate(float delta, Scene scene, Input input)
    {
    
    }
    
    @Override
    public void update(float delta, Scene scene, Input input)
    {
    
    }
    
    @Override
    public void updatePhysics(float delta, Scene scene, Input input)
    {
    
    }
}
