package com.anthonycosenza;

import com.anthonycosenza.input.Input;
import com.anthonycosenza.input.Key;
import com.anthonycosenza.input.KeyAction;
import com.anthonycosenza.rendering.Renderer;
import com.anthonycosenza.shape.Pyramid3;
import com.anthonycosenza.projection.Projection;

public class Project
{
    Scene scene;
    float rotation = 0;
    float moveSpeed = 100f;
    float rotationSpeed = 100;
    float xPixelsPerDegree = 100;
    float yPixelsPerDegree = 80;
    
    public Project()
    {
        scene = new Scene();
        Model model = new Model(new Pyramid3(50, 30));
        Entity entity = model.createEntity();
        
        entity.setPosition(0, 0, -50);
        scene.addEntity(entity);
    }
    
    
    
    public void physicsUpdate(float delta, Input input)
    {
        rotation += rotationSpeed * delta;
        rotation = rotation % 360;
        for(Entity entity1 : scene.getEntities())
        {
            //entity1.rotate(1, 1, 1, rotation);
        }
        if(input.getState(Key.A) == KeyAction.PRESSED || input.getState(Key.A) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalX(-moveSpeed * delta);
        }
        if(input.getState(Key.D) == KeyAction.PRESSED || input.getState(Key.D) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalX(moveSpeed * delta);
        }
        if(input.getState(Key.W) == KeyAction.PRESSED || input.getState(Key.W) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalZ(moveSpeed * delta);
        }
        if(input.getState(Key.S) == KeyAction.PRESSED || input.getState(Key.S) == KeyAction.REPEAT)
        {
            scene.getCamera().moveLocalZ(-moveSpeed * delta);
        }
        
        if(!input.isCursorStale())
        {
            scene.getCamera().rotateDeg(input.getMouseDirection().mult(-1 / 10f));
        }
        //scene.getCamera().rotateDeg(new Vector2(1, 0));
        //System.out.println("Pos: " + input.getMousePosition());
        //System.out.println("Dir: " + input.getMouseDirection());
    
        //scene.getCamera().moveLocalX(-rotation);
    
    }
    
    public void render(double delta, Renderer renderer, Projection projection)
    {
        renderer.render(scene, projection);
    }
}
