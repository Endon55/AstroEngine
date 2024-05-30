package com.anthonycosenza.editor;

import com.anthonycosenza.editor.scene.nodes.LoadWindowNode;
import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.SceneManager;

import java.util.List;

public class Editor
{
    
    private Engine engine;
    private ProjectSettings editorSettings;
    
    private static List<String> recentProjects = EditorIO.getRecentProjects();
    public Editor()
    {
        /*
         * Create a user directory to store persistent editor information.
         */
        editorSettings = new ProjectSettings();
        loadEditorSettings(editorSettings);
        engine = new Engine(editorSettings);
        SceneManager.setScene(new LoadWindowNode(engine));
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
    
    public static List<String> getRecentProjects()
    {
        return recentProjects;
    }
}
