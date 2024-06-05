package com.anthonycosenza;

import com.anthonycosenza.editor.Editor;
import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.SceneManager;

import java.io.IOException;

public class Main
{

    public static void main(String[] args) throws InterruptedException, IOException, IllegalAccessException
    {
        if(args.length != 0)
        {
            String projectDirectory = args[0];
            ProjectSettings settings = EditorIO.loadProjectData(projectDirectory);
            Engine engine = new Engine(settings, false);
            long mainSceneID = settings.mainScene;
            AssetManager.setAssetPath(false, EditorIO.getAssetDirectory());
            SceneManager.setScene(AssetManager.getInstance().instantiateScene(mainSceneID));
            engine.run();
        }
        else {
            Editor editor = new Editor();
        }
    }
}