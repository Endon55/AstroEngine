package com.anthonycosenza.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.game.IAppLogic;
import com.anthonycosenza.engine.render.Material;
import com.anthonycosenza.engine.render.Model;
import com.anthonycosenza.engine.render.ModelLoader;
import com.anthonycosenza.engine.render.Render;
import com.anthonycosenza.engine.render.Texture;
import com.anthonycosenza.engine.scene.Camera;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.render.Mesh;
import com.anthonycosenza.engine.window.Window;
import org.joml.Vector2f;

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
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cubeEntity;
    private float rotation;
    
    @Override
    public void init(Window window, Scene scene, Render render)
    {
        Model cubeModel = ModelLoader.loadModel("cube-model", System.getProperty("user.dir") + "/resources/models/cube/cube.obj", scene.getTextureCache()); //"resources/models/cube/cube.obj"
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
    
    }
    
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis)
    {
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
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
        
        
    }
    
    @Override
    public void update(Window window, Scene scene, float interval)
    {
        rotation += 1.5;
        if(rotation > 360)
        {
            rotation = 0;
        }
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();
    }

    @Override
    public void cleanup()
    {
        //Nothing Yet
    }
    
}
