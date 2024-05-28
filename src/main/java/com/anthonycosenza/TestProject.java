package com.anthonycosenza;

import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.util.math.EngineMath;

public class TestProject extends Project
{
    float rotation = 0;
    float moveSpeed = 10f;
    final float minMoveSpeed = 10f;
    final float maxMoveSpeed = 1000f;
    final float moveSpeedIncrement = 2f;
    
    float rotationSpeed = 100;
    float mouseSensitivity = 20;
    final float minMouseSensitivity = 0;
    final float maxMouseSensitivity = 1000;
    final float mouseIncrement = 10;
    
    public TestProject()
    {
        super("Testbench");
    }
    
    
    @Override
    public void settings(ProjectSettings settings)
    {
        settings.width = 1500;
        settings.height = 750;
    }
    
    public void initialize(int width, int height)
    {
        
        Scene scene = new Scene();
        addScene(scene);
    
        /*Mesh mesh = ShapeBuilder.plane(1000, 1000);
        Model square = new Model(mesh, new Texture("resources/images/Ai Sasha.png"));
        Entity entity = square.createEntity();
        entity.setPosition(0, 0, -10f);
        scene.add(entity);
        scene.getCamera().setPosition(0, 50, 0);*/
        
        Model cube = ModelLoader.loadModel("AstroEngine/resources/assets/boat/BoatFBX.fbx", 0);
        Entity cubeE = cube.createEntity();
        
/*        scene.add(cubeE.spawnInstance());
        scene.add(cubeE.spawnInstance(20, 50, 100));
        scene.add(cubeE.spawnInstance(-20, 50, 100));
        scene.add(cubeE.spawnInstance(20, -50, 100));
        scene.add(cubeE.spawnInstance(-20, -50, 100));*/
    }
    
    public void uiUpdate(float delta, Scene scene, Input input)
    {
        //ImGui.showDemoWindow();
    }
    
    public void update(float delta, Scene scene, Input input)
    {
        boolean sprint = input.getState(Key.LEFT_SHIFT) == KeyAction.PRESSED || input.getState(Key.RIGHT_SHIFT) == KeyAction.PRESSED;
        float deltaSpeed = (moveSpeed * delta);
        if(sprint) deltaSpeed *= 2f;
        
        if(input.isPressed(Key.A))
        {
            scene.getCamera().moveLocalX(-deltaSpeed);
        }
        if(input.isPressed(Key.D))
        {
            scene.getCamera().moveLocalX(deltaSpeed);
        }
        if(input.isPressed(Key.W))
        {
            scene.getCamera().moveLocalZ(-deltaSpeed);
        }
        if(input.isPressed(Key.S))
        {
            scene.getCamera().moveLocalZ(deltaSpeed);
        }
        //Down
        if(input.isPressed(Key.C))
        {
            scene.getCamera().moveGlobalY(-deltaSpeed);
        }
        //Up
        if(input.isPressed(Key.SPACE))
        {
            scene.getCamera().moveGlobalY(deltaSpeed);
        }
        
        
        if(input.getScrollPosition() != 0)
        {
            moveSpeed += moveSpeedIncrement * input.getScrollPosition();
            moveSpeed = EngineMath.clamp(moveSpeed, minMoveSpeed, maxMoveSpeed);
        }
        
        if(!input.isCursorStale() && input.isMouseLocked())
        {
            float mouseDelta = mouseSensitivity * delta;
            scene.getCamera()
                    .rotateDeg(input.getMouseDirection().x() * mouseDelta, input.getMouseDirection().y() * mouseDelta);
        }
    }
    
    public void updatePhysics(float delta, Scene scene, Input input)
    {
        rotation += rotationSpeed * delta;
        //rotation = rotation % 360;
        
        /*for(Entity entity : scene.getEntities())
        {
            entity.rotate(1, 1, 1, rotation);
        }*/
        
    }
    
    
}