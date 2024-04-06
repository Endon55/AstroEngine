package com.anthonycosenza.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.game.IAppLogic;
import com.anthonycosenza.engine.render.Material;
import com.anthonycosenza.engine.render.Model;
import com.anthonycosenza.engine.render.ModelLoader;
import com.anthonycosenza.engine.render.Render;
import com.anthonycosenza.engine.render.Texture;
import com.anthonycosenza.engine.render.gui.IGuiInstance;
import com.anthonycosenza.engine.render.light.PointLight;
import com.anthonycosenza.engine.render.light.SceneLighting;
import com.anthonycosenza.engine.render.light.SpotLight;
import com.anthonycosenza.engine.scene.Camera;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Fog;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.render.Mesh;
import com.anthonycosenza.engine.scene.SkyBox;
import com.anthonycosenza.engine.window.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class TestAppLogic implements IAppLogic
{
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cubeEntity;
    private float rotation;
    private LightControls lightControls;
    
    @Override
    public void init(Window window, Scene scene, Render render)
    {
        String quadModelId = "quad-model";
        Model quadModel = ModelLoader.loadModel(quadModelId, System.getProperty("user.dir") + "/resources/models/quad/quad.obj", scene.getTextureCache());
        scene.addModel(quadModel);
        
        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        terrainEntities = new Entity[numRows][numCols];
        for(int j = 0; j < numRows; j++)
        {
            for(int i = 0; i < numCols; i++)
            {
                Entity entity = new Entity("TERRAIN_" + j + "_" + i, quadModelId);
                terrainEntities[j][i] = entity;
                scene.addEntity(entity);
            }
        }
        
        Model cubeModel = ModelLoader.loadModel("cube-model", System.getProperty("user.dir") + "/resources/models/cube/cube.obj", scene.getTextureCache()); //"resources/models/cube/cube.obj"
        scene.addModel(cubeModel);
        
        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        cubeEntity.updateModelMatrix();
        scene.addEntity(cubeEntity);
        
        SceneLighting sceneLighting = new SceneLighting();
        sceneLighting.getAmbientLight().setIntensity(.3f);
        scene.setSceneLighting(sceneLighting);
        
        SkyBox skyBox = new SkyBox(System.getProperty("user.dir") + "/resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.setScale(100);
        scene.setSkyBox(skyBox);
        
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.95f));
        
        scene.getCamera().moveUp(0.1f);
        updateTerrain(scene);
        
    }
    
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed)
    {
        //We don't want to process the same input command repeatedly, so we leave the call if it's already been used.
        if(inputConsumed)
        {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        
        if(window.isKeyPressed(GLFW_KEY_W))
        {
            camera.moveForward(move);
        }
        else if(window.isKeyPressed(GLFW_KEY_S))
        {
            camera.moveBackwards(move);
        }
        if(window.isKeyPressed(GLFW_KEY_A))
        {
            camera.moveLeft(move);
        }
        else if(window.isKeyPressed(GLFW_KEY_D))
        {
            camera.moveRight(move);
        }
        if(window.isKeyPressed(GLFW_KEY_SPACE))
        {
            camera.moveUp(move);
        }
        else if(window.isKeyPressed(GLFW_KEY_C))
        {
            camera.moveDown(move);
        }
        
        MouseInput mouseInput = window.getMouseInput();
        if(mouseInput.isRightButtonPressed())
        {
            Vector2f displVec = mouseInput.getDisplayVector();
            camera.addRotation((float) -Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) -Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
        
        
    }
    
    @Override
    public void update(Window window, Scene scene, float interval)
    {
        updateTerrain(scene);
    }

    public void updateTerrain(Scene scene)
    {
        int cellSize = 10;
        Camera camera = scene.getCamera();
        Vector3f cameraPos = camera.getPosition();
        int cellCol = (int) (cameraPos.x / cellSize);
        int cellRow = (int) (cameraPos.z / cellSize);
        
        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        int zOffset = -NUM_CHUNKS;
        float scale = cellSize / 2.0f;
        for(int j = 0; j < numRows; j++)
        {
            int xOffset = -NUM_CHUNKS;
            for(int i = 0; i < numCols; i++)
            {
                Entity entity = terrainEntities[j][i];
                entity.setScale(scale);
                entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
                entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
                xOffset++;
            }
            zOffset++;
        }
    }
    
    
    @Override
    public void cleanup()
    {
        //Nothing Yet
    }
}
