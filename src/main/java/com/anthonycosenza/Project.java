package com.anthonycosenza;

import com.anthonycosenza.input.Input;
import com.anthonycosenza.input.Key;
import com.anthonycosenza.input.KeyAction;
import com.anthonycosenza.rendering.Renderer;
import com.anthonycosenza.shape.Pyramid3;
import com.anthonycosenza.transformation.Projection;

public class Project
{
    Scene scene;
    float rotation = 0;
    float moveSpeed = .1f;
    float rotationSpeed = 100;
    
    public Project()
    {
        scene = new Scene();
        Model model = new Model(new Pyramid3(50, 30));
        Entity entity = model.createEntity();
        entity.setPosition(0, 0, -100);
        scene.addEntity(entity);
        scene.getCamera().setRotationDeg(0, 0, 0);
    }
    
    
    
    public void physicsUpdate(double delta, Input input)
    {
        //System.out.println("D: " + delta);
        rotation += rotationSpeed * delta;
        rotation = rotation % 360;
        for(Entity entity1 : scene.getEntities())
        {
            entity1.rotate(1, 1, 1, rotation);
        }
        
        //scene.getCamera().setRotationDeg(0, rotation, 0);
        if(input.getState(Key.A) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalX(-moveSpeed);
        }
        if(input.getState(Key.D) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalX(moveSpeed);
        }
        if(input.getState(Key.W) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalZ(moveSpeed);
        }
        if(input.getState(Key.S) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalZ(-moveSpeed);
        }
    
    
        //scene.getCamera().moveLocalZ(-rotation);
    
    }
    
    public void render(double delta, Renderer renderer, Projection projection)
    {
        renderer.render(scene, projection);
    }
}
