package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.Main;
import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.editor.scene.popups.AssetCreationPopup;
import com.anthonycosenza.editor.scene.popups.ImportPopup;
import com.anthonycosenza.editor.scene.popups.MaterialEditorPopup;
import com.anthonycosenza.editor.scene.popups.NodeViewerPopup;
import com.anthonycosenza.editor.scene.popups.Popup;
import com.anthonycosenza.editor.scene.popups.ProjectSettingsPopup;
import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.AssetType;
import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.rendering.materials.texture.ImageTexture;
import com.anthonycosenza.engine.space.rendering.materials.texture.Texture;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.space.node._3d.MoveableCamera;
import com.anthonycosenza.engine.space.rendering.FrameBuffer;
import com.anthonycosenza.engine.space.rendering.SceneRenderer;
import com.anthonycosenza.engine.ui.AstroFonts;
import com.anthonycosenza.engine.util.ImGuiUtils;
import com.anthonycosenza.engine.util.FileType;
import com.anthonycosenza.engine.util.Toml;
import com.anthonycosenza.engine.util.math.Color;
import com.anthonycosenza.engine.util.math.EngineMath;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiSliderFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
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
    private final long doubleClickInterval = 250;
    
    private final Engine engine;
    private final Texture folderIcon;
    private final Texture settingsIcon;
    private final Texture textIcon;
    private final Texture codeIcon;
    private final Texture sceneIcon;
    private final Texture modelIcon;
    private final Texture materialIcon;
    private final Texture projectIcon;
    private final Texture textureIcon;
    private final FrameBuffer viewportFrameBuffer;
    private final SceneRenderer sceneRenderer;
    
    float[] assetBrowserScale = new float[]{.5f};
    private final float defaultAssetBrowserSize = 100f;
    private final float assetBrowserPadding = 50;
    private final int defaultTableColor = ImColor.rgba(0, 0, 0, 0);
    private final int hoveredTableColor = ImColor.rgba(22, 71, 62, 255);
    private final int selectedTableColor = ImColor.rgba(22, 57, 71, 255);
    private final Color propertyTableColor = new Color(.2f, .6f, .8f);
    private int assetBrowserFileSelected = -1;
    private File assetBrowserPath = EditorIO.getProjectDirectory();
    private long assetBrowserLastClickTime = -1;

    private boolean hadIni;
    private boolean firstDockBuild = true;
    
    int headerFontSize = 24;
    String defaultFont = AstroFonts.DEFAULT_FONT;
    
    private Scene sceneManagerNode;
    private Node sceneManagerSelected;
    private boolean modified = false;
    final Color astroColor = new Color(20, 13, 35, 255);
    private Popup popup;
    
    private Camera camera;
    private Process projectProcess;
    private long lastSceneSave = 0;
    private final long minSaveTime = 5000;
    
    public EditorNode(Engine engine, boolean hadIni)
    {
        super();
        sceneRenderer = new SceneRenderer();
        this.engine = engine;
        name = "Editor";

        camera = new MoveableCamera();
        folderIcon = new ImageTexture("AstroEngine/resources/icons/folder.png");
        settingsIcon = new ImageTexture("AstroEngine/resources/icons/settingsPaper.png");
        textIcon = new ImageTexture("AstroEngine/resources/icons/txtPaper.png");
        codeIcon = new ImageTexture("AstroEngine/resources/icons/codePaper.png");
        sceneIcon = new ImageTexture("AstroEngine/resources/icons/diagram.png");
        textureIcon = new ImageTexture("AstroEngine/resources/icons/picture.png");
        modelIcon = new ImageTexture("AstroEngine/resources/icons/modeling.png");
        projectIcon = new ImageTexture("AstroEngine/resources/icons/project.png");
        materialIcon = new ImageTexture("AstroEngine/resources/icons/paint-bucket.png");
        viewportFrameBuffer = new FrameBuffer(1920, 1080);
        
        this.hadIni = hadIni;
    
        
    }
    
    @Override
    public void initialize()
    {
        super.initialize();
        long mainScene = EditorIO.getProjectSettings().mainScene;
        if(mainScene != -1)
        {
            Scene scene = AssetManager.getInstance().instantiateScene(mainScene);
            if(scene == null)
            {
                ProjectSettings settings = EditorIO.getProjectSettings();
                settings.mainScene = -1;
                Toml.updateProjectSettings(settings);
            }
            else loadScene(scene);
        }
    }
    
    
    int left;
    int right;
    int center;
    int bottom;
    int top;
    @Override
    public void updateUI(float delta)
    {
        if(projectProcess != null && !projectProcess.isAlive())
        {
            projectProcess.destroy();
            projectProcess = null;
        }
        
        if(modified && (System.currentTimeMillis() - lastSceneSave) >= minSaveTime)
        {
            Toml.updateScene(sceneManagerNode);
            modified = false;
            lastSceneSave = System.currentTimeMillis();
        }

        createMainDockspace();
    
        ImGui.pushStyleColor(ImGuiCol.WindowBg, astroColor.getInt());
        ImGui.setNextWindowDockID(top, ImGuiCond.FirstUseEver);
        createCommandBar();
        
        ImGui.setNextWindowDockID(left, ImGuiCond.FirstUseEver);
        createSceneTree();
    
        ImGui.setNextWindowDockID(right, ImGuiCond.FirstUseEver);
        createPropertyInspector();
    
        ImGui.setNextWindowDockID(bottom, ImGuiCond.FirstUseEver);
        createAssetBrowser();
        
        ImGui.setNextWindowDockID(center, ImGuiCond.FirstUseEver);
        createSceneViewport();
        ImGui.popStyleColor();

        if(hasPopup())
        {
            updatePopups();
        }
        
    }
    
    private void updatePopups()
    {
        if(popup.isFinished())
        {
            if(popup instanceof NodeViewerPopup nodeViewerPopup)
            {
                sceneManagerNode.addChild(nodeViewerPopup.finish());
                modified = true;
            }
            else if(popup instanceof ImportPopup importPopup)
            {
                AssetManager.getInstance().importAsset(new File(importPopup.finish()));
            }
            else if(popup instanceof ProjectSettingsPopup settingsPopup)
            {
                Toml.updateProjectSettings(settingsPopup.finish());
            }
            else if(popup instanceof AssetCreationPopup assetPopup)
            {
                loadAsset(assetPopup.finish());
            }
            else if(popup instanceof MaterialEditorPopup)
            {
                modified = true;
            }
        
            popup = null;
        }
        else popup.create();
    }
    private void loadAsset(Asset asset)
    {
        if(asset instanceof Scene scene)
        {
            sceneManagerNode = scene;
        }
    }
    
    final float commandHeight = .08f;
    private void createMainDockspace()
    {
        ImGuiViewport viewport = ImGui.getMainViewport();
        int dockspaceConfig = ImGuiDockNodeFlags.PassthruCentralNode | imgui.internal.flag.ImGuiDockNodeFlags.NoCloseButton | imgui.internal.flag.ImGuiDockNodeFlags.NoTabBar;
        int mainWindowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBringToFrontOnFocus;
        mainWindowFlags |= ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoDocking;
    
        if((dockspaceConfig & ImGuiDockNodeFlags.PassthruCentralNode) == ImGuiDockNodeFlags.PassthruCentralNode)
        {
            mainWindowFlags |= ImGuiWindowFlags.NoBackground;
        }
    
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeY());
        ImGui.setNextWindowPos(viewport.getPosX(), viewport.getPosY() + viewport.getSizeY() * commandHeight);
        ImGui.setNextWindowViewport(viewport.getID());
    
        ImGui.begin("Dockspace", mainWindowFlags);
    
        ImGui.popStyleVar(3);
        int dockspaceID = ImGui.getID("MyDockspace");
    
        ImGui.dockSpace(dockspaceID, 0f, 0f, dockspaceConfig);
        
        if(!hadIni && firstDockBuild)
        {
            firstDockBuild = false;
        
            imgui.internal.ImGui.dockBuilderRemoveNode(dockspaceID);
            imgui.internal.ImGui.dockBuilderAddNode(dockspaceID, dockspaceConfig | imgui.internal.flag.ImGuiDockNodeFlags.DockSpace);
            imgui.internal.ImGui.dockBuilderSetNodeSize(dockspaceID, viewport.getSizeX(), viewport.getSizeY());
            imgui.internal.ImGui.dockBuilderSetNodePos(dockspaceID, viewport.getPosX(), viewport.getPosY());
        
            ImInt id = new ImInt(dockspaceID);
            top = imgui.internal.ImGui.dockBuilderSplitNode(id.get(), ImGuiDir.Up, commandHeight, null, id);
            left = imgui.internal.ImGui.dockBuilderSplitNode(id.get(), ImGuiDir.Left, .15f, null, id);
            right = imgui.internal.ImGui.dockBuilderSplitNode(id.get(), ImGuiDir.Right, .2f, null, id);
            bottom = imgui.internal.ImGui.dockBuilderSplitNode(id.get(), ImGuiDir.Down, .3f, null, id);
    
            center = dockspaceID;
            
            imgui.internal.ImGui.dockBuilderFinish(id.get());
        }
    
        ImGui.end();
    }
    
    private void createCommandBar()
    {
        int frameConfig = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize;
        
        ImGuiViewport viewport = ImGui.getMainViewport();
        ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getPosY() + viewport.getSizeY() * commandHeight);
        if(ImGui.begin("Command bar", frameConfig))
        {
            if(ImGui.beginMenuBar())
            {
                if(ImGui.beginMenu("File"))
                {
                    if(ImGui.selectable("Project Settings"))
                    {
                        if(popup != null)
                        {
                            throw new RuntimeException("Can't open a popup when one is already open.");
                        }
                        else popup = new ProjectSettingsPopup(EditorIO.getProjectSettings());
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
            
            ImGui.text("Play");
            ImGui.sameLine();
            
            if(projectProcess != null)
            {
                ImGui.pushStyleColor(ImGuiCol.Button, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0);
                ImGui.arrowButton("PlayButton", 1);
                ImGui.popStyleColor();
                ImGui.popStyleColor();
                ImGui.popStyleColor();
            }
            else
            {
                if(ImGui.arrowButton("PlayButton", 1))
                {
                    try
                    {
                        String mainFolder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                                .toURI()).getPath();
            
                        ProcessBuilder builder = new ProcessBuilder("java", "-jar", mainFolder, EditorIO
                                .getProjectDirectory().getAbsolutePath());
                        projectProcess = builder.start();
                    } catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    } catch(URISyntaxException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
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
    
    private boolean hasPopup()
    {
        return popup != null;
    }

    private void loadScene(Scene scene)
    {
        //SceneManager.setScene(scene);
        sceneManagerNode = scene;
        sceneManagerSelected = sceneManagerNode;
    }
    private void createSceneTree()
    {
        int treeConfig = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen;
        int frameConfig = ImGuiWindowFlags.AlwaysHorizontalScrollbar | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse;
        if(ImGui.begin("Scene Tree", frameConfig))
        {
            AstroFonts.push(defaultFont, headerFontSize);
            ImGui.text(ImGuiUtils.centerAlignOffset("Scene Tree", ImGui.getContentRegionAvailX()));
            AstroFonts.pop();
            ImGui.separator();
            ImGui.pushStyleColor(ImGuiCol.Border, ImColor.rgba(1, 0, 0, 1f));
            if(sceneManagerNode == null)
            {
                ImGui.text(ImGuiUtils.centerAlignOffset("Load scene or create new one", ImGui.getContentRegionAvailX()));
                if(ImGui.button("Create new Scene"))
                {
                    popup = new AssetCreationPopup(AssetType.SCENE, getAssetBrowserPath());
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
                ImGui.sameLine();
                if(ImGui.button("Set Main"))
                {
                    ImGui.beginTooltip();
                    ImGui.setTooltip("Set as the starting scene your game will load into");
                    ProjectSettings settings = EditorIO.getProjectSettings();
                    settings.mainScene = sceneManagerNode.resourceID;
                    Toml.updateProjectSettings(settings);
                    ImGui.endTooltip();
                }
                ImGui.separator();
                
                
                drawTree(sceneManagerNode, treeConfig);
            }
        }
        ImGui.popStyleColor();
        ImGui.end();
    }
    
    
    private void createPropertyInspector()
    {
        int frameConfig = ImGuiWindowFlags.NoMove;
        if(ImGui.begin("Property Inspector", frameConfig))
        {
            AstroFonts.push(defaultFont, headerFontSize);
            ImGui.text(ImGuiUtils.centerAlignOffset("Property Inspector", ImGui.getContentRegionAvailX()));
            ImGui.separator();
            AstroFonts.pop();
            
            Node selectedNode = sceneManagerSelected;
            if(selectedNode != null)
            {
                //Header
                ImGui.text("id: " + selectedNode.resourceID);
                String className = selectedNode.getClass().getSimpleName();
                ImGui.text(className + ".class");
                ImGui.separator();
                
                //Creating a dropdown for each superclass
                Class<? extends Node> nodeClass = selectedNode.getClass();
                while(nodeClass != null && !Object.class.equals(nodeClass))
                {
                    List<Field> fields = Arrays.stream(nodeClass.getDeclaredFields())
                            .filter(field -> !Modifier.isTransient(field.getModifiers()) &&
                                    !field.getName().equals("parent") &&
                                    !field.getName().equals("children") &&
                                    !field.getName().equals("resourceID")).toList();
                    if(!fields.isEmpty())
                    {
                        if(ImGui.collapsingHeader(nodeClass.getSimpleName() + " Properties", ImGuiTreeNodeFlags.DefaultOpen))//set as scene name
                        {
                            ImBoolean modified = new ImBoolean(false);
    
                            EditorProperty.propertyTable(nodeClass, selectedNode, modified, astroColor);
                            
                            if(modified.get()) this.modified = true;
                        }
                    }
                    nodeClass = (Class<? extends Node>) nodeClass.getSuperclass();
                }
            }
        }
        ImGui.end();
    }
    
    
    
    public File getAssetBrowserPath()
    {
        return assetBrowserPath;
    }
    int scaleSliderWidth = 150;
    private void createAssetBrowser()
    {
        File projectDirectory = EditorIO.getProjectDirectory();
        int frameConfig = ImGuiWindowFlags.AlwaysVerticalScrollbar;
        
        if(ImGui.begin("Asset Browser", frameConfig))
        {
            AstroFonts.push(defaultFont, headerFontSize);
            ImGui.text("Asset Browser");
            ImGui.separator();
            AstroFonts.pop();
            //Import button.
            ImGui.dummy(10, 0);
            ImGui.sameLine();
            if(ImGui.button("Import Asset"))
            {
                if(popup != null) System.out.println("Finish what you're doing first");
                else popup = new ImportPopup();
            }
            ImGui.sameLine();
            if(ImGui.button("Create New"))
            {
                if(popup != null) System.out.println("Finish what you're doing first");
                else popup = new AssetCreationPopup(getAssetBrowserPath());
            }
            
            ImGui.sameLine();
            ImGui.dummy(10, 0);
            
            ImGui.sameLine();
            String truncatedPath = assetBrowserPath.getAbsolutePath().replace(projectDirectory.getAbsolutePath(), "");
            if(truncatedPath.isEmpty())
            {
                truncatedPath = "project:\\\\";
            }
            else truncatedPath = "project:\\" + truncatedPath;
            
            
            ImGui.sameLine();
            ImGui.setNextItemWidth(scaleSliderWidth);
            ImGui.sliderFloat("##-scale", assetBrowserScale, 0.1f, 1.0f, "%.1f", ImGuiSliderFlags.AlwaysClamp | ImGuiSliderFlags.NoInput);
    
            ImGui.sameLine();
            //Remove background
            ImGui.pushStyleColor(ImGuiCol.Button, ImColor.rgba(0, 0, 0, 0));
            if(ImGui.arrowButton("##-upButton", 2))
            {
                if(!assetBrowserPath.equals(EditorIO.getProjectDirectory()))
                {
                    assetBrowserPath = assetBrowserPath.getParentFile();
                }
            }
            ImGui.popStyleColor();
            
            ImGui.sameLine();
            ImGui.text(truncatedPath);
            
            ImGui.separator();
            
            
            float cellSize = (defaultAssetBrowserSize * assetBrowserScale[0]) + assetBrowserPadding;
            int columns = EngineMath.clamp((int) (ImGui.getWindowContentRegionMax().x / cellSize), 1, 64);
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
                                case MODEL -> modelIcon.getTextureID();
                                case PROJECT -> projectIcon.getTextureID();
                                case MATERIAL -> materialIcon.getTextureID();
                                case TEXTURE -> textureIcon.getTextureID();
                                case DIRECTORY -> folderIcon.getTextureID();
                                case TEXT -> textIcon.getTextureID();
                                case SCENE -> sceneIcon.getTextureID();
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
                                    loadScene(AssetManager.getInstance().instantiateScene(child));
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
                    ImGuiUtils.createDragAndDropSource(child.getName(), child);
                    ImGui.popID();
                    fileNameWithExtension = ImGuiUtils.centerAlignOffset(fileNameWithExtension, columnWidth);
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
