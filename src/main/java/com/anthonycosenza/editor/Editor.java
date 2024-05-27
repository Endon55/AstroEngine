package com.anthonycosenza.editor;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.space.ProjectSettings;

public class Editor
{
    
    private Engine engine;
    private EditorProject editorProject;
    private ProjectSettings editorSettings;
    public Editor()
    {
        /*
         * Create a user directory to store persistent editor information.
         */
        editorSettings = new ProjectSettings();
        loadEditorSettings(editorSettings);
        editorProject = new EditorProject(editorSettings);
        
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
    

    public static String getEditorIniFile()
    {
        return "settings";
    }
}
