package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.editor.scene.SaveType;
import com.anthonycosenza.editor.scene.popups.NodeViewerPopup;
import com.anthonycosenza.editor.scene.popups.Popup;
import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.space.node._3d.Model3D;
import com.anthonycosenza.engine.space.node._3d.MoveableCamera;
import com.anthonycosenza.engine.space.rendering.FrameBuffer;
import com.anthonycosenza.engine.space.rendering.SceneRenderer;
import com.anthonycosenza.engine.ui.UITools;
import com.anthonycosenza.engine.util.FileType;
import com.anthonycosenza.engine.util.math.vector.Vector2i;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImDouble;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

public class EditorNode extends Node
{
    private static int MAX_STRING_FIELD_LENGTH = 20;
    
    private final long doubleClickInterval = 250;
    
    private final Engine engine;
    private final Texture folderIcon;
    private final Texture settingsIcon;
    private final Texture textIcon;
    private final Texture codeIcon;
    private final Texture upArrow;
    private final FrameBuffer viewportFrameBuffer;
    private final SceneRenderer sceneRenderer;
    
    private float assetBrowserScale = 1f;
    private final float defaultAssetBrowserSize = 100f;
    private final float assetBrowserPadding = 50;
    private final int defaultTableColor = ImColor.rgba(0, 0, 0, 0);
    private final int hoveredTableColor = ImColor.rgba(0, 0, 255, 255);
    private final int selectedTableColor = ImColor.rgba(255, 0, 0, 255);
    private int assetBrowserFileSelected = -1;
    private File assetBrowserPath = EditorIO.getProjectDirectory();
    private long assetBrowserLastClickTime = -1;
    
    private Node sceneManagerNode;
    private Node sceneManagerSelected;
    private Node testScene;
    private boolean modified = false;
    private Popup popup;
    Camera camera;
    
    public EditorNode(Engine engine)
    {
        super();
        sceneRenderer = new SceneRenderer();
        this.engine = engine;
        name = "Editor";
        testScene = new Node("Scene");
        Model model = ModelLoader.loadModel("AstroEngine/resources/assets/boat/BoatFBX.fbx", 0);
        Model3D model3d = new Model3D();
        model3d.model = model;
        model3d.name = "Boat1";
        model3d.setPosition(0, 0, 50);
        testScene.addChild(model3d);
        camera = new MoveableCamera();
        //camera.moveGlobalZ(50);
        testScene.addChild(camera);
        model3d = new Model3D();
        model3d.model = model;
        model3d.name = "Boat2";
        model3d.setPosition(0, 0, -50);
        testScene.addChild(model3d);
        //testScene.addChild(new Mesh3D());
        folderIcon = new Texture("AstroEngine/resources/icons/folder.png");
        settingsIcon = new Texture("AstroEngine/resources/icons/settingsPaper.png");
        textIcon = new Texture("AstroEngine/resources/icons/txtPaper.png");
        codeIcon = new Texture("AstroEngine/resources/icons/codePaper.png");
        upArrow = new Texture("AstroEngine/resources/icons/arrowhead-up.png");
        viewportFrameBuffer = new FrameBuffer(1920, 1080);
        
    }
    
    @Override
    public void initialize()
    {
    
    }
    
    
    
    @Override
    public void updateUI(float delta)
    {
        modified = false;
        int dockspaceConfig = ImGuiDockNodeFlags.PassthruCentralNode;
        int mainDock = ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), dockspaceConfig);
    
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createSceneManager();
    
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createPropertyInspector();
    
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createAssetBrowser();
        
        ImGui.setNextWindowDockID(mainDock, ImGuiCond.FirstUseEver);
        createSceneViewport();
        
        if(createSaveWindow)
        {
            createSaveWindow();
        }
        if(hasPopup())
        {
            if(popup.isFinished())
            {
                if(popup instanceof NodeViewerPopup nodeViewerPopup)
                {
                    sceneManagerNode.addChild(nodeViewerPopup.finish());
                }
                
                popup = null;
            }
            else popup.create();
        }
        
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
    
    boolean createSaveWindow = false;
    boolean saveWindowShouldOpen = false;
    Vector2i saveWindowSize = new Vector2i(350, 150);
    SaveType[] saveWindowTypes = SaveType.values();
    String[] saveWindowTypesString = Arrays.stream(saveWindowTypes).map(Enum::name).toArray(String[]::new);
    int saveWindowTypeSelection = 0;
    ImString saveWindowString = new ImString(20);
    String error = "";
    
    
    private void createSaveWindow()
    {
        int frameConfig = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse |
                        ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoTitleBar;
        
        ImGui.setNextWindowSize(saveWindowSize.x(), saveWindowSize.y());
        ImGui.setNextWindowPos(ImGui.getMainViewport().getCenterX() - saveWindowSize.x() * .5f, ImGui.getMainViewport().getCenterY() - saveWindowSize.y() * .5f);
        
        if(ImGui.begin("Save As", frameConfig))
        {
            ImGui.text("Save As...");
            ImGui.separator();
            //ImGui.spacing();
            
            //float projSize = ImGui.calcTextSize("proj:\\\\").x + ImGui.getStyle().getCellPadding().x;
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
            ImGui.setNextItemWidth(ImGui.getWindowSizeX());
            if(!error.isEmpty())
            {
                UITools.error(error);
            }
            if(ImGui.inputText("##2", saveWindowString, ImGuiInputTextFlags.EnterReturnsTrue))
            {
                SaveType saveType = saveWindowTypes[saveWindowTypeSelection];
                String filename =
                        EditorIO.getProjectDirectory().getPath() + "/" + saveWindowString.get() + saveType.getExtension();
                File file = new File(filename);
                if(file.exists())
                {
                    error = "File name already in use";
                }
                else
                {
                    switch(saveType)
                    {
                        case Scene ->
                        {
                            Scene scene = new Scene();
                            scene.name = saveWindowString.get();
                            EditorIO.serialize(file, scene);
                            if(saveWindowShouldOpen)
                            {
                                loadScene(scene);
                            }
                        }
                    }
                    saveWindowString.set("");
                    error = "";
                    saveWindowTypeSelection = 0;
                    createSaveWindow = false;
                }
            }
            ImGui.popStyleVar();
            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();
            
                for(int i = 0; i < saveWindowTypesString.length; i++)
                {
                    boolean isSelected = saveWindowTypeSelection == i;
                    if(ImGui.selectable(saveWindowTypesString[i], isSelected))
                    {
                        saveWindowTypeSelection = i;
                        System.out.println("Selection: " + i);
                    }
                    if(isSelected)
                    {
                        ImGui.setItemDefaultFocus();
                    }
                }
        }
        ImGui.end();
    }
    
    private boolean hasPopup()
    {
        return popup != null;
    }

    private void loadScene(Scene scene)
    {
        sceneManagerNode = scene;
        sceneManagerSelected = sceneManagerNode;
    }
    
    private void createSceneManager()
    {
        int treeConfig = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen;
        int frameConfig = ImGuiWindowFlags.AlwaysHorizontalScrollbar;
        if(ImGui.begin("Scene Tree", frameConfig))
        {
            if(sceneManagerNode == null)
            {
                ImGui.text("Load scene or create new one");
                if(ImGui.button("Create new Scene"))
                {
                    createSaveWindow = true;
                }
            }
            else
            {
                //Always default to the top level node
                if(sceneManagerSelected == null)
                {
                    sceneManagerSelected = sceneManagerNode;
                }
                
                
                if(ImGui.button("+"))
                {
                    if(!hasPopup())
                    {
                        popup = new NodeViewerPopup();
                    }
                }
                
                
                drawTree(sceneManagerNode, treeConfig);
            }
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
                //Header
                String className = selectedNode.getClass().getSimpleName();
                ImGui.text(className + ".class");
                ImGui.separator();
                
                //Creating a dropdown for each superclass
                Class<? extends Node> nodeClass = selectedNode.getClass();
                while(nodeClass != null && !Object.class.equals(nodeClass))
                {
                    List<Field> fields = Arrays.stream(nodeClass.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(Property.class) &&
                                    !field.getName().equals("parent") &&
                                    !field.getName().equals("children")).toList();
                    if(!fields.isEmpty())
                    {
                        if(ImGui.collapsingHeader(nodeClass.getSimpleName() + " Properties"))//set as scene name
                        {
                            for(Field field : fields)
                            {
                                field.setAccessible(true);
    
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
                                        if(ImGui.inputInt("##", imValue))
                                        {
                                            field.set(selectedNode, imValue.get());
                                            modified = true;
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
                                        if(ImGui.inputInt("##", imValue))
                                        {
                                            field.set(selectedNode, imValue.get());
                                            modified = true;
                                        }
                                    }
                                    else if(Float.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = 0f;
                                        }
                                        ImFloat imValue = new ImFloat((Float) value);
                                        if(ImGui.inputFloat("##", imValue))
                                        {
                                            field.set(selectedNode, imValue.get());
                                            modified = true;
                                        }
                                    }
                                    else if(Double.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = 0d;
                                        }
                                        ImDouble imValue = new ImDouble((Double) value);
                                        if(ImGui.inputDouble("##", imValue))
                                        {
                                            field.set(selectedNode, imValue.get());
                                            modified = true;
                                        }
                                    }
                                    else if(Vector3f.class.equals(field.getType()))
                                    {
                                        float[] imValue = new float[3];
                                        Vector3f vector;
                                        if(value != null)
                                        {
                                            vector = ((Vector3f) value);
                                            imValue[0] = vector.x();
                                            imValue[1] = vector.y();
                                            imValue[2] = vector.z();
                                        }
                                        else
                                        {
                                            vector = new Vector3f();
                                            field.set(selectedNode, vector);
                                        }
                                        if(ImGui.inputFloat3("##", imValue))
                                        {
                                            vector.set(imValue[0], imValue[1], imValue[2]);
                                            modified = true;
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
                                        else
                                        {
                                            quaternion = new Quaternionf();
                                            field.set(selectedNode, quaternion);
                                        }
                                        if(ImGui.inputFloat3("##", imValue))
                                        {
                                            quaternion.set(imValue[0], imValue[1], imValue[2], 1);
                                            modified = true;
                                        }
                                    }
                                    else if(String.class.equals(field.getType()))
                                    {
                                        if(value == null)
                                        {
                                            value = "";
                                        }
                                        //Pre-allocating the String buffer, otherwise the buffers size is limited to the length of whatever was first added to it.
                                        ImString imValue = new ImString(MAX_STRING_FIELD_LENGTH);
                                        imValue.set(value);
                                        if(ImGui.inputText("##", imValue))
                                        {
                                            if(ImGui.isItemDeactivatedAfterEdit())
                                            {
                                                field.set(selectedNode, imValue.get());
                                                modified = true;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        ImGui.text("Implement - " + field.getType().getSimpleName());
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
    
    private void createAssetBrowser()
    {
        File projectDirectory = EditorIO.getProjectDirectory();
        int frameConfig = ImGuiWindowFlags.AlwaysVerticalScrollbar;
        
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
            int columns = (int) (ImGui.getWindowContentRegionMax().x / cellSize);
            float columnWidth = (ImGui.getColumnWidth()) / columns;
            
            if(ImGui.beginTable("Asset Viewer", columns, 0))
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
                                case TEXT, SCENE -> textIcon.getTextureID();
                            };
                    //It gets confused thinking that all the buttons are the same so we push a specific ID for it.
                    ImGui.pushID(i);
                    if(ImGui.imageButton(textureID, cellSize, cellSize, 0f, 0f, 1f, 1f))
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
                                }
                                else if(type == FileType.SCENE)
                                {
                                    loadScene((Scene) EditorIO.deserialize(child));
                                }
                                else System.out.println("Not a directory");
                                
                                assetBrowserFileSelected = -1;
                                assetBrowserLastClickTime = -1;
                                
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
    
    private void createSceneViewport()
    {
        int frameConfig = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
        
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f);
        if(ImGui.begin("Scene Viewport", frameConfig))
        {
            int windowWidth = engine.getWindow().getWidth();
            int windowHeight = engine.getWindow().getHeight();
            
            ImVec2 contentRegion = ImGui.getWindowContentRegionMax();
            int width = (int) contentRegion.x;
            int height = (int) (contentRegion.y - ImGui.getFrameHeight());
            viewportFrameBuffer.resize(width, height);
            
            viewportFrameBuffer.bind();
            
    
            glClearColor(0f, 1f, 0f, 0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
            
            if(sceneManagerNode != null)
            {
                sceneRenderer.render(sceneManagerNode, camera, engine.getProjection());
            }
            
            ImGui.image(viewportFrameBuffer.getTextureID(), width, height);
            viewportFrameBuffer.unbind();
    
    
            glViewport(0, 0, windowWidth, windowHeight);
            
            
        }
        ImGui.popStyleVar();
        //ImGui.popStyleVar();
        ImGui.end();
    }
    
}
