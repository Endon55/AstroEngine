package com.anthonycosenza.editor;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.util.NativeFileDialogue;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorProject extends Project
{
    private Project userProject;
    private boolean isRealProject;
    
    private boolean creatingProject = false;
    private ImString newProjectName = new ImString();
    private String newProjectDirectory = "";
    private String error = "";
    private List<String> recentProjects;
    
    public EditorProject(Project userProject, ProjectSettings editorSettings, boolean isRealProject)
    {
        super(userProject.getProjectName(), editorSettings);
        this.userProject = userProject;
        this.isRealProject = isRealProject;
    }
    
    
    @Override
    public void initialize(int width, int height)
    {
        recentProjects = EditorIO.getRecentProjects();
        
        
        if(isRealProject)
        {
            
            ImGuiIO io = ImGui.getIO();
            userProject.initialize(width, height);
            io.setIniFilename(Editor.getEditorIniFile() + ".ini");
            io.setWantSaveIniSettings(true);
        }

        //ImGui.saveIniSettingsToDisk(Editor.getEditorIniFile() + ".ini");
    }
    
    
    
    
    public void loadProject(String projectDirectory)
    {
        initialize(getSettings().width, getSettings().height);
        EditorIO.loadProject(projectDirectory);
        isRealProject = true;
        newProjectName = new ImString();
        newProjectDirectory = "";
        error = "";
    }
    
    @Override
    public void uiUpdate(float delta, Scene scene, Input input)
    {
        //Checking if we need to load a new project.
        if(isRealProject)
        {
            int dockspaceConfig = ImGuiDockNodeFlags.PassthruCentralNode;
            int mainDock = ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), dockspaceConfig);
    
            ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
            createSceneManager();
    
            ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
            createPropertyInspector();
        }
        else
        {
            createLoadProject();
        }

    }
    private void createLoadProject()
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
                    newProjectDirectory = NativeFileDialogue.open();
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
                        ProjectFileUtil.createNewProject(projectDirectory);
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
                for(String projectPath : recentProjects)
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
    String selectedProject = "";
    private void createSceneManager()
    {
        int frameConfig = 0;
        if(ImGui.begin("Scene Hierarchy", frameConfig))
        {
            if(ImGui.collapsingHeader("Scene"))//set as scene name
            {
                ImGui.indent();
                ImGui.bulletText("Child1");
                ImGui.bulletText("Child2");
                ImGui.bulletText("Child3");
                ImGui.bulletText("Child4");
                ImGui.bulletText("Child5");
            }
        }
        ImGui.end();
    }
    
    private void createPropertyInspector()
    {
        int frameConfig = 0;
        if(ImGui.begin("Scene Hierarchy2", frameConfig))
        {
            if(ImGui.collapsingHeader("Scene2"))//set as scene name
            {
                ImGui.indent();
                ImGui.bulletText("Child1");
            }
        }
        ImGui.end();
    }
    
    private void createAssetManager()
    {
    
    }
    
    private void createLivePreview()
    {
    
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
