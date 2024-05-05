package com.anthonycosenza;

import com.anthonycosenza.input.Input;
import com.anthonycosenza.input.Key;
import com.anthonycosenza.input.KeyAction;
import com.anthonycosenza.math.vector.Vector2;
import com.anthonycosenza.math.vector.Vector3;
import com.anthonycosenza.projection.Projection2d;
import com.anthonycosenza.rendering.Renderer;
import com.anthonycosenza.rendering.TextRenderer;
import com.anthonycosenza.shape.Pyramid3;
import com.anthonycosenza.projection.Projection3d;
import com.anthonycosenza.shape.ShapeBuilder;
import com.anthonycosenza.text.Font;
import com.anthonycosenza.text.TextStrip;

public class Project
{
    Scene scene;
    float rotation = 0;
    float moveSpeed = 100f;
    float rotationSpeed = 100;
    float xPixelsPerDegree = 100;
    float yPixelsPerDegree = 80;
    Font font;
    
    public Project()
    {
        scene = new Scene();
        //Model model = new Model(new Pyramid3(50, 30));
        //Entity entity = model.createEntity();
        
        //entity.setPosition(0, 0, -50);
        //scene.addEntity(entity);
        
        //font = new Font("resources/fonts/Bagnard.otf");
        //scene.getTextStrips().add(new TextStrip("Anthony", 10, new Vector3(100, 100, 100), new Vector2(100, 200), font));
        
        Mesh mesh = ShapeBuilder.square();
        Model square = new Model(mesh, new Texture("resources/images/Ai Sasha.png"));
        Entity entity = square.createEntity();
        entity.setPosition(0, 0, -.5f);
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
            scene.getCamera().rotateDeg(input.getMouseDirection().x() / 10, input.getMouseDirection().y() / 10);
        }
        //scene.getCamera().rotateDeg(new Vector2(1, 0));
        //System.out.println("Pos: " + input.getMousePosition());
        //System.out.println("Dir: " + input.getMouseDirection());
    
        //scene.getCamera().moveLocalX(-rotation);
    
    }
    
    public void render(double delta, Renderer renderer, TextRenderer textRenderer, Projection3d projection3d, Projection2d projection2d)
    {
        renderer.render(scene, projection3d);
        textRenderer.render(scene, projection2d);
    }
}
