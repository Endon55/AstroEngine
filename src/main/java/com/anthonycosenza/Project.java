package com.anthonycosenza;

import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;
import com.anthonycosenza.engine.space.shape.ShapeBuilder;
import imgui.ImGui;

public class Project
{
    Scene scene;
    float rotation = 0;
    float moveSpeed = 1f;
    float rotationSpeed = 100;
    float mouseSensitivity = 100;
    
    public Project(int width, int height)
    {
        scene = new Scene();
    
        Mesh mesh = ShapeBuilder.square(10, 10);
        //TextureAtlas atlas = FontAtlasGenerator.getAtlas(10, font);
        //TextureAtlas atlas = new TextureAtlas("resources/images/Ai Sasha.png");
        //Model square = new Model(mesh, atlas.getTexture(100, 100, 200, 200));
        Model square = new Model(mesh, new Texture("resources/images/Ai Sasha.png"));
        //Model square = new Model(mesh, new Texture("resources/images/Ai Sasha.png"));
        Entity entity = square.createEntity();
        entity.setPosition(0, 0, -10f);
        scene.add(entity);
        

    }
    
    
    
    public void uiUpdate(double delta, Input input)
    {
        ImGui.showDemoWindow();
    }
    
    public void update(float delta, Input input)
    {
        boolean sprint = input.getState(Key.LEFT_SHIFT) == KeyAction.PRESSED || input.getState(Key.RIGHT_SHIFT) == KeyAction.PRESSED;
        float deltaSpeed = (moveSpeed * delta);
        if(sprint) deltaSpeed *= 2f;
        
        if(input.getState(Key.A) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalX(-deltaSpeed);
        }
        if(input.getState(Key.D) == KeyAction.PRESSED || input.getState(Key.D) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalX(deltaSpeed);
        }
        if(input.getState(Key.W) == KeyAction.PRESSED || input.getState(Key.W) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalZ(deltaSpeed);
        }
        if(input.getState(Key.S) == KeyAction.PRESSED || input.getState(Key.S) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalZ(-deltaSpeed);
        }
    
        if(!input.isCursorStale() && input.isMouseLocked())
        {
            float mouseDelta = mouseSensitivity * delta;
            scene.getCamera().rotateDeg(input.getMouseDirection().x() * mouseDelta, input.getMouseDirection().y() * mouseDelta);
        }
    }
    public void physicsUpdate(float delta, Input input)
    {
        rotation += rotationSpeed * delta;
        rotation = rotation % 360;
        
        for(Entity entity1 : scene.getEntities())
        {
            //entity1.rotate(1, 1, 1, rotation);
        }

    }
    
    
    public Scene getScene()
    {
        return scene;
    }
}
