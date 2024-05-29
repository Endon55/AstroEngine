package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node._3d.Mesh3D;
import com.anthonycosenza.engine.space.node._3d.Model3D;
import com.anthonycosenza.engine.space.node._3d.MoveableCamera;
import com.anthonycosenza.engine.ui.UITools;
import com.anthonycosenza.engine.util.FileType;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableFlags;

import java.io.File;
import java.util.Objects;

public class EditorNode extends Node
{
    private Texture folderIcon;
    private Texture settingsIcon;
    private Texture textIcon;
    private Texture codeIcon;
    
    private float assetBrowserScale = 1f;
    private float defaultAssetBrowserSize = 100f;
    private float assetBrowserPadding = 50;
    
    public EditorNode()
    {
        super();
        Model model = ModelLoader.loadModel("AstroEngine/resources/assets/boat/BoatFBX.fbx", 0);
        Model3D model3d = new Model3D();
        model3d.model = model;
        model3d.name = "Boat1";
        model3d.setPosition(0, 0, 50);
        addChild(model3d);
        Camera camera = new MoveableCamera();
        //camera.moveGlobalZ(50);
        addChild(camera);
        model3d = new Model3D();
        model3d.model = model;
        model3d.name = "Boat2";
        model3d.setPosition(0, 0, -50);
        addChild(model3d);
        addChild(new Mesh3D());
        folderIcon = new Texture("AstroEngine/resources/icons/folder.png");
        settingsIcon = new Texture("AstroEngine/resources/icons/settingsPaper.png");
        textIcon = new Texture("AstroEngine/resources/icons/txtPaper.png");
        codeIcon = new Texture("AstroEngine/resources/icons/codePaper.png");
    }
    
    @Override
    public void initialize()
    {

    }
    
    @Override
    public void updateUI(float delta)
    {
        int dockspaceConfig = ImGuiDockNodeFlags.PassthruCentralNode;
        int mainDock = ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), dockspaceConfig);
    
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createSceneManager();
    
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createPropertyInspector();
    
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createAssetBrowser();
    }
    int defaultTableColor = ImColor.rgba(0, 0, 0, 0);
    int hoveredTableColor = ImColor.rgba(0, 0, 255, 255);
    int selectedTableColor = ImColor.rgba(255, 0, 0, 255);
    int fileSelected = -1;
    File browserPath = EditorIO.getProjectDirectory();
    long lastClickTime = -1;
    long doubleClickInterval = 250;
    private void createAssetBrowser()
    {
        File projectDirectory = EditorIO.getProjectDirectory();
        int frameConfig = 0;
        
        if(ImGui.begin("Asset Browser", frameConfig))
        {
            String truncatedPath = browserPath.getAbsolutePath().replace(projectDirectory.getAbsolutePath(), "");
            truncatedPath = "project:" + truncatedPath;
            if(ImGui.arrowButton("upDirButton", 2))
            {
                if(!browserPath.equals(EditorIO.getProjectDirectory()))
                {
                    browserPath = browserPath.getParentFile();
                }
            }
            ImGui.sameLine();
            ImGui.text(truncatedPath);
            ImGui.separator();
            
            
            
            float size = (defaultAssetBrowserSize * assetBrowserScale) + assetBrowserPadding;
            
            int columns = (int) (ImGui.getWindowWidth() / size);
            float columnWidth = (ImGui.getColumnWidth()) / columns;
            if(ImGui.beginTable("Asset Viewer", columns, ImGuiTableFlags.NoClip))
            {
                
                ImGui.tableNextColumn();
                ImGui.pushStyleColor(ImGuiCol.Button, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0);
                File[] files = Objects.requireNonNull(browserPath.listFiles());
                for(int i = 0; i < files.length; i++)
                {
                    File child = files[i];
                    String[] split = child.getAbsolutePath().split("\\\\");
                    String fileNameWithExtension = split[split.length - 1];
                    
                    if(i == fileSelected)
                    {
                        ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, selectedTableColor);
                    }
                    FileType type = FileType.getFileType(child);
                    int textureID = switch(type)
                            {
                                case SETTINGS -> settingsIcon.getTextureID();
                                case CODE -> codeIcon.getTextureID();
                                case DIRECTORY -> folderIcon.getTextureID();
                                case TEXT -> textIcon.getTextureID();
                            };
                    
                    //It gets confused thinking that all the buttons are the same so we push a specific ID for it.
                    ImGui.pushID(i);
                    
                    if(ImGui.imageButton(textureID, size, size))
                    {
                        if(fileSelected == i)
                        {
                            long now = System.currentTimeMillis();
                            long dif = now - lastClickTime;
                            
                            if(dif < doubleClickInterval)
                            {
                                if(type == FileType.DIRECTORY)
                                {
                                    browserPath = child;
                                    fileSelected = -1;
                                    lastClickTime = -1;
                                }
                                else System.out.println("Not a directory");
                            }
                            else
                            {
                                lastClickTime = now;
                            }
                        }
                        else
                        {
                            lastClickTime = System.currentTimeMillis();
                            fileSelected = i;
                        }
                        
                    }
                    ImGui.popID();
                    
                    fileNameWithExtension = UITools.centerAlignOffset(fileNameWithExtension, columnWidth);
                    ImGui.text(fileNameWithExtension);
                    
                    ImGui.tableNextColumn();
                }
                ImGui.popStyleColor();
                ImGui.popStyleColor();
                ImGui.popStyleColor();
                
                ImGui.endTable();
            }
        }
        ImGui.end();
    }
    
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
        if(ImGui.begin("Property Inspector", frameConfig))
        {
            if(ImGui.collapsingHeader("Scene2"))//set as scene name
            {
                ImGui.indent();
                ImGui.bulletText("Child1");
            }
        }
        ImGui.end();
    }
}
