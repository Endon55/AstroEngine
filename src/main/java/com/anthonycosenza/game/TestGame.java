package com.anthonycosenza.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.game.IGameLogic;
import com.anthonycosenza.engine.render.Material;
import com.anthonycosenza.engine.render.Model;
import com.anthonycosenza.engine.render.Texture;
import com.anthonycosenza.engine.scene.Camera;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.render.Mesh;
import com.anthonycosenza.engine.window.Window;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class TestGame implements IGameLogic
{
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.05f;
    Scene scene;
    Entity cube1;
    
    @Override
    public void init(Window window)
    {
        scene = new Scene(window.getWidth(), window.getHeight());
        
        Material material = new Material();
        Texture texture = scene.getTextureCache().createTexture("/models/MinecraftDirtTexture.png");
        material.setTexturePath(texture.getTexturePath());
        
        Mesh cubeMesh = new Mesh(new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        
                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,
        
                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,
        
                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,
        
                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        }, new float[]{
        
                // Top Face
                0.25f, 0.25f,
                0.25f, 0.5f,
                0.5f, 5f,
                0.5f, .25f,
                
                //Bottom Face
                0.75f, 0.25f,
                0.75f, 0.5f,
                0.75f, 0.75f,
                1f, 0.25f,
        
                //Side 1 Face
                0.0f, 0.0f,
                0.25f, 0.0f,
                0.0f, 0.25f,
                0.25f, 0.25f,
                
                //Side 1 Face
                0.0f, 0.0f,
                0.25f, 0.0f,
                0.0f, 0.25f,
                0.25f, 0.25f,
        
                //Side 2 Face
                0.0f, 0.0f,
                0.25f, 0.0f,
                0.0f, 0.25f,
                0.25f, 0.25f,
        
                //Side 3 Face
                0.0f, 0.0f,
                0.25f, 0.0f,
                0.0f, 0.25f,
                0.25f, 0.25f,
      
    
        },new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7}, texture);
    
        material.getMeshList().add(cubeMesh);
        
        scene.addModel("CubeModel", new Model("CubeMesh", List.of(material)));
        cube1 = new Entity("Cube1", "CubeModel");
        cube1.setPosition(0.0f, 0.0f, -10f);
        cube1.updateModelMatrix();
        scene.addEntity(cube1);
    
    }
    
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis)
    {
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        
        if(window.isKeyPressed(GLFW_KEY_W))
        {
            camera.moveForward(move);
            System.out.println("W Pressed");
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
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
        
        
    }
    
    @Override
    public void update(Window window, Scene scene, float interval)
    {
        //Quaternionf quat = cube1.getRotation();
        
        //cube1.setPosition(cube1.getPosition().x - .01f, 0, 0);
        //cube1.setRotation(.5f, 0.5f, 0.0f, quat.angle() + .1f);
        //cube1.setRotationRad(1.0f, 0.0f, 0.0f, 25);
        //cube1.updateModelMatrix();
    }
    
    @Override
    public void render(Window window)
    {
    
    }
    
    @Override
    public void cleanup()
    {
    
    }
    
    @Override
    public Scene getScene()
    {
        return scene;
    }
}
