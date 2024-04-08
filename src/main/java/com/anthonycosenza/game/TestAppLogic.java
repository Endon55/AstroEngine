package com.anthonycosenza.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.game.IAppLogic;
import com.anthonycosenza.engine.render.light.AmbientLight;
import com.anthonycosenza.engine.render.model.Model;
import com.anthonycosenza.engine.render.model.ModelLoader;
import com.anthonycosenza.engine.render.Render;
import com.anthonycosenza.engine.render.light.DirectionalLight;
import com.anthonycosenza.engine.render.light.SceneLighting;
import com.anthonycosenza.engine.render.model.animation.AnimationData;
import com.anthonycosenza.engine.scene.Camera;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Fog;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.scene.SkyBox;
import com.anthonycosenza.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class TestAppLogic implements IAppLogic
{
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.1f;
    private Entity cubeEntity;
    private float rotation;
    private LightControls lightControls;
    private AnimationData animationData;
    private float lightAngle;
    
    @Override
    public void init(Window window, Scene scene, Render render)
    {
/*        Model cube = ModelLoader.loadModel("cube-model", "resources/models/burningcube/Burning_Cube_by_3DHaupt-(Wavefront OBJ).obj", scene.getTextureCache(), false);
        Entity cubeEntity = new Entity("cube-entity", cube.getId());
        cubeEntity.setScale(50);
        cubeEntity.setPosition(0, 0, 50);
        cubeEntity.updateModelMatrix();
        scene.addModel(cube);
        scene.addEntity(cubeEntity);*/
        
        String terrainModelId = "terrain";
        Model terrainModel = ModelLoader.loadModel(terrainModelId, "resources/models/terrain/terrain.obj",
                scene.getTextureCache(), false);
        scene.addModel(terrainModel);
        Entity terrainEntity = new Entity("terrainEntity", terrainModelId);
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);
    
        String bobModelId = "bobModel";
        Model bobModel = ModelLoader.loadModel(bobModelId, "resources/models/bob/boblamp.md5mesh",
                scene.getTextureCache(), true);
        scene.addModel(bobModel);
        Entity bobEntity = new Entity("bobEntity", bobModelId);
        bobEntity.setScale(0.05f);
        bobEntity.updateModelMatrix();
        animationData = new AnimationData(bobModel.getAnimationList().get(0));
        bobEntity.setAnimationData(animationData);
        scene.addEntity(bobEntity);
    
        SceneLighting sceneLights = new SceneLighting();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);
    
        DirectionalLight dirLight = sceneLights.getDirectionalLight();
        dirLight.setDirection(0, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLighting(sceneLights);
    
        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.setScale(100);
        scene.setSkyBox(skyBox);
    
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));
    
        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));
    
        lightAngle = 0;
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
       animationData.nextFrame();
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
