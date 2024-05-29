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
import imgui.flag.ImGuiTreeNodeFlags;

import java.io.File;
import java.util.Objects;

public class EditorNode extends Node
{
    private final long doubleClickInterval = 250;
    
    private final Texture folderIcon;
    private final Texture settingsIcon;
    private final Texture textIcon;
    private final Texture codeIcon;
    private final Texture upArrow;
    
    private float assetBrowserScale = 1f;
    private final float defaultAssetBrowserSize = 100f;
    private final float assetBrowserPadding = 50;
    private final int defaultTableColor = ImColor.rgba(0, 0, 0, 0);
    private final int hoveredTableColor = ImColor.rgba(0, 0, 255, 255);
    private final int selectedTableColor = ImColor.rgba(255, 0, 0, 255);
    private int assetBrowserFileSelected = -1;
    private File assetBrowserPath = EditorIO.getProjectDirectory();
    private long assetBrowserLastClickTime = -1;
    
    private Node sceneManagerNode = this;
    private Node sceneManagerSelected = null;
    
    public EditorNode()
    {
        super();
        name = "Editor";
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
        upArrow = new Texture("AstroEngine/resources/icons/arrowhead-up.png");
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

    private void createAssetBrowser()
    {
        File projectDirectory = EditorIO.getProjectDirectory();
        int frameConfig = 0;
        
        if(ImGui.begin("Asset Browser", frameConfig))
        {
            String truncatedPath = assetBrowserPath.getAbsolutePath().replace(projectDirectory.getAbsolutePath(), "");
            if(truncatedPath.isEmpty())
            {
                truncatedPath = "project:\\\\";
            }
            else truncatedPath = "project:\\" + truncatedPath;
            if(ImGui.imageButton(upArrow.getTextureID(), 10, 10))
            {
                if(!assetBrowserPath.equals(EditorIO.getProjectDirectory()))
                {
                    assetBrowserPath = assetBrowserPath.getParentFile();
                }
            }
            ImGui.sameLine();
            ImGui.text(truncatedPath);
            ImGui.separator();
            
            
            float cellSize = (defaultAssetBrowserSize * assetBrowserScale) + assetBrowserPadding;
            int columns = (int) (ImGui.getWindowWidth() / cellSize);
            float columnWidth = (ImGui.getColumnWidth()) / columns;
            
            if(ImGui.beginTable("Asset Viewer", columns, ImGuiTableFlags.NoClip))
            {
                
                ImGui.tableNextColumn();
                ImGui.pushStyleColor(ImGuiCol.Button, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, hoveredTableColor);
                File[] files = Objects.requireNonNull(assetBrowserPath.listFiles());
                for(int i = 0; i < files.length; i++)
                {
                    File child = files[i];
                    String[] split = child.getAbsolutePath().split("\\\\");
                    String fileNameWithExtension = split[split.length - 1];
                    
                    if(i == assetBrowserFileSelected)
                    {
                        ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, selectedTableColor);
                    }
                    else
                    {
                        ImGui.tableSetBgColor(ImGuiTableBgTarget.CellBg, defaultTableColor);
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
                    
                    if(ImGui.imageButton(textureID, cellSize, cellSize))
                    {
                        if(assetBrowserFileSelected == i)
                        {
                            long now = System.currentTimeMillis();
                            long dif = now - assetBrowserLastClickTime;
                            
                            if(dif < doubleClickInterval)
                            {
                                if(type == FileType.DIRECTORY)
                                {
                                    assetBrowserPath = child;
                                    assetBrowserFileSelected = -1;
                                    assetBrowserLastClickTime = -1;
                                }
                                else System.out.println("Not a directory");
                            }
                            else
                            {
                                assetBrowserLastClickTime = now;
                            }
                        }
                        else
                        {
                            assetBrowserLastClickTime = System.currentTimeMillis();
                            assetBrowserFileSelected = i;
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
    
    private void drawTree(Node node, int flags)
    {
        boolean hasChildren = node.children != null && !node.children.isEmpty();
        String name = (node.name.isEmpty() ? "" + node.getId() : node.name);
        
        if(ImGui.treeNodeEx(node.getId(), flags |
                (hasChildren ? 0 : ImGuiTreeNodeFlags.Leaf) |
                (node.equals(sceneManagerSelected) ? ImGuiTreeNodeFlags.Selected : 0), name))
        {
            if(ImGui.isItemClicked())
            {
                sceneManagerSelected = node;
            }
            
            if(hasChildren)
            {
                for(Node child : node.children)
                {
                    drawTree(child, flags);
                }
            }
            ImGui.treePop();
        }
    }
    
    private void createSceneManager()
    {
        int treeConfig = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        if(ImGui.begin("Scene Tree", 0))
        {
            
            drawTree(sceneManagerNode, treeConfig);
            /*String name = (sceneManagerNode.name.isEmpty() ? "" + sceneManagerNode.getId() : sceneManagerNode.name);
            if(ImGui.treeNodeEx(name, treeConfig))
            {
                if(sceneManagerNode.children != null)
                {
                    for(Node child : sceneManagerNode.children)
                    {
                        drawTree(child, treeConfig);
                    }
                }
                ImGui.treePop();
            }*/
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
