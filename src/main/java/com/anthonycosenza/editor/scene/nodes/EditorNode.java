package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node._3d.Mesh3D;
import com.anthonycosenza.engine.space.node._3d.Model3D;
import com.anthonycosenza.engine.space.node._3d.MoveableCamera;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;

public class EditorNode extends Node
{
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
}