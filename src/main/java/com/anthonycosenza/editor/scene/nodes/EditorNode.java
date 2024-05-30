package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.annotations.Property;
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
import imgui.type.ImDouble;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
            
            
            if(hasChildren)
            {
                for(Node child : node.children)
                {
                    drawTree(child, flags);
                }
            }
            ImGui.treePop();
        }
        if(ImGui.isItemClicked())
        {
            sceneManagerSelected = node;
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
            Node selectedNode = sceneManagerSelected;
            if(selectedNode != null)
            {
                String className = selectedNode.getClass().getSimpleName();
                ImGui.text(className + ".class");
                ImGui.separator();
                Class<? extends Node> nodeClass = selectedNode.getClass();
                while(nodeClass != null && !Object.class.equals(nodeClass))
                {
                    if(ImGui.collapsingHeader(nodeClass.getSimpleName() + " Properties"))//set as scene name
                    {
                        for(Field field : nodeClass.getDeclaredFields())
                        {
                            if(Modifier.isPublic(field.getModifiers()) && field.canAccess(selectedNode) && field.isAnnotationPresent(Property.class))
                            {
                                ImGui.text(field.getName());
                                ImGui.sameLine();
                                try
                                {
                                    Object value = field.get(selectedNode);
                                    
                                    if(Integer.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = 0;
                                        }
                                        ImInt imValue = new ImInt((Integer) value);
                                        if(ImGui.inputInt("", imValue))
                                        {
                                            System.out.println("int changed");
                                        }
                                    }
                                    else if(Long.class.equals(field.getType()))
                                    {
                                        //ImGui doesn't directly support longs, hopefully this doesn't cause problems lol.
                                        if(value == null)
                                        {
                                            value = 0;
                                        }
                                        ImInt imValue = new ImInt((Integer) value);
                                        if(ImGui.inputInt("", imValue))
                                        {
                                            System.out.println("int changed");
                                        }
                                    }
                                    else if(Float.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = 0f;
                                        }
                                        ImFloat imValue = new ImFloat((Float) value);
                                        if(ImGui.inputFloat("", imValue))
                                        {
                                            System.out.println("int changed");
                                        }
                                    }
                                    else if(Double.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = 0d;
                                        }
                                        ImDouble imValue = new ImDouble((Double) value);
                                        if(ImGui.inputDouble("", imValue))
                                        {
                                            System.out.println("int changed");
                                        }
                                    }
                                    else if(Vector3f.class.equals(field.getType()))
                                    {
                                        float[] imValue = new float[3];
                                        Vector3f vector;
                                        if(value != null)
                                        {
                                            vector = ((Vector3f) value);
                                            imValue[0] =  vector.x();
                                            imValue[1] = vector.y();
                                            imValue[2] = vector.z();
                                        }
                                        else vector = new Vector3f();
                                        if(ImGui.inputFloat3(field.getName(), imValue))
                                        {
                                            vector.set(imValue[0], imValue[1], imValue[2]);
                                        }
                                    }
                                    else if(Quaternionf.class.equals(field.getType()))
                                    {
                                        float[] imValue = new float[3];
                                        Quaternionf quaternion;
                                        if(value != null)
                                        {
                                            quaternion = ((Quaternionf) value);
                                            imValue[0] = quaternion.x();
                                            imValue[1] = quaternion.y();
                                            imValue[2] = quaternion.z();
                                        }
                                        else quaternion = new Quaternionf();
                                        if(ImGui.inputFloat3(field.getName(), imValue))
                                        {
                                            quaternion.set(imValue[0], imValue[1], imValue[2], 1);
                                        }
                                    }
                                    else if(String.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = "";
                                        }
                                        ImString imValue = new ImString((String)value);
                                        if(ImGui.inputText(field.getName(), imValue))
                                        {
                                            System.out.println("String changed");
                                        }
                                    }
                                    else {
                                        if(value == null)
                                        {
                                            value = "";
                                        }
                                        ImGui.text(value.toString());
                                    }
                                } catch(IllegalAccessException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        
                    }
                    
                    nodeClass = (Class<? extends Node>) nodeClass.getSuperclass();
                }
                
                
            }
        }
        ImGui.end();
    }
}
