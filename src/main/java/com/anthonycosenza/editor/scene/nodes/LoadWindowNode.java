package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.editor.Editor;
import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.SceneManager;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.util.NativeFileDialogue;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.io.File;

public class LoadWindowNode extends Node
{
    private boolean creatingProject = false;
    private ImString newProjectName = new ImString();
    private String newProjectDirectory = "";
    private String error = "";
    private String selectedProject = "";
    private boolean shouldAutoLoad = true;
    private Engine engine;
    
    public LoadWindowNode(Engine engine)
    {
        this.engine = engine;
    }
    
    @Override
    public void initialize()
    {
        if(shouldAutoLoad)
        {
            String mostRecent = Editor.getRecentProjects().get(0);
            if(mostRecent != null)
            {
                loadProject(mostRecent);
            }
        }
    }

    public void loadProject(String projectDirectory)
    {
        EditorIO.loadProject(projectDirectory);
        AssetManager.setAssetPath(false, EditorIO.getAssetDirectory());
        newProjectName = new ImString();
        newProjectDirectory = "";
        error = "";
        boolean hadIni = EditorIO.getGuiINI().exists();
        ImGuiIO io = ImGui.getIO();
        ImGui.loadIniSettingsFromDisk(EditorIO.getGuiINI().getPath());
        io.setIniFilename(EditorIO.getGuiINI().getPath());
        io.setWantSaveIniSettings(true);
        SceneManager.setScene(new EditorNode(engine, hadIni));
    }
    
    @Override
    public void updateUI(float delta)
    {
        int frameConfig = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse;
        ImVec2 viewPortSize = ImGui.getMainViewport().getSize();
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowSize(viewPortSize.x / 2, viewPortSize.y / 2);
        ImGui.setNextWindowPos(center.x - viewPortSize.x / 4, center.y - viewPortSize.y / 4);
        if(creatingProject)
        {
            if(ImGui.begin("Create Project", frameConfig))
            {
                ImGui.text("Create New Project");
                //Insert a list of a project cache.
                ImGui.separator();
                //Returns the status of being pressed
                ImGui.inputText("Project Name", newProjectName);
                if(ImGui.button("Location"))
                {
                    newProjectDirectory = NativeFileDialogue.openFolder();
                }
                ImGui.text((newProjectDirectory.isEmpty() ? "" : newProjectDirectory + "\\" + newProjectName.get()));
                if(ImGui.button("Create Project", 0, 0))
                {
                    if(newProjectDirectory.isEmpty())
                    {
                        error = "A directory must be specified";
                    }
                    else if(newProjectName.isEmpty())
                    {
                        error = "A project name must be specified";
                    }
                    String projectPath = newProjectDirectory + "\\" + newProjectName.get();
                    File projectDirectory = new File(projectPath);
                    if(projectDirectory.exists())
                    {
                        error = "Project file path is not empty";
                    }
                    else
                    {
                        EditorIO.createNewProject(projectDirectory);
                        loadProject(projectPath);
                        //now I need to reload the engine with the new Project.
                    
                    }
                }
                if(!error.isEmpty())
                {
                    ImGui.pushStyleColor(ImGuiCol.Text, ImColor.rgba(255, 0, 0, 255));
                    ImGui.text(error);
                    ImGui.popStyleColor();
                }
            }
            ImGui.end();
        }
        else
        {
            if(ImGui.begin("Load Project", frameConfig))
            {
                ImGui.text("Load Project");
                //Insert a list of a project cache.
                ImGui.separator();
                for(String projectPath : Editor.getRecentProjects())
                {
                    //ImGui.text(projectPath);
                    if(ImGui.selectable(projectPath, selectedProject.equals(projectPath)))
                    {
                        selectedProject = projectPath;
                    }
                }
            
                if(ImGui.button("Load Project", 0, 0))
                {
                    loadProject(selectedProject);
                }
                ImGui.sameLine();
                if(ImGui.button("Create Project", 0, 0))
                {
                    creatingProject = true;
                }
    
    
            }
            ImGui.end();
        }
    }
}
