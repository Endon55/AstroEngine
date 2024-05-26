package com.anthonycosenza.editor;

import com.anthonycosenza.Main;
import com.anthonycosenza.Project;
import com.anthonycosenza.TestProject;
import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;

public class Editor
{
    private Engine engine;
    private EditorProject editorProject;
    private ProjectSettings editorSettings;
    private Project userProject;
    public Editor()
    {
        userProject = new TestProject();
        editorSettings = new ProjectSettings();
        loadEditorSettings(editorSettings);
        editorProject = new EditorProject(userProject, editorSettings);
        
        engine = new Engine(editorProject);
        engine.run();
    }
    
    public void loadEditorSettings(ProjectSettings settings)
    {
        //Read from ini file.
        settings.width = 1920;
        settings.height = 1080;
        settings.enableSystemDiagnostics = false;
        //settings.width = 2560;
        //settings.height = 1440;
    }
    

    public static Project getBlankProject()
    {
        return new Project("Untitled")
        {
            @Override
            public void settings(ProjectSettings settings)
            {
                settings.width = 1500;
                settings.height = 750;
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
        };
    }
    
    public static String getEditorIniFile()
    {
        return "settings";
    }
}
