package com.anthonycosenza;

import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.entity.texture.atlas.CanvasAtlas;
import com.anthonycosenza.engine.space.entity.texture.atlas.FontAtlasGenerator;
import com.anthonycosenza.engine.space.rendering.Scene;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.space.shape.ShapeBuilder;
import com.anthonycosenza.engine.loader.text.Font;
import com.anthonycosenza.engine.util.math.Color;

public class Project
{
    Scene scene;
    float rotation = 0;
    float moveSpeed = 1f;
    float rotationSpeed = 100;
    Font font;
    
    public Project(int width, int height)
    {
        scene = new Scene();
/*        Model model = new Model(new Pyramid3(50, 30));
        Entity entity = model.createEntity();
        
        entity.setPosition(0, 0, -50);
        scene.add(entity);*/
    
    
        Mesh mesh = ShapeBuilder.square(10, 10);
        //TextureAtlas atlas = FontAtlasGenerator.getAtlas(10, font);
        //TextureAtlas atlas = new TextureAtlas("resources/images/Ai Sasha.png");
        //Model square = new Model(mesh, atlas.getTexture(100, 100, 200, 200));
        Model square = new Model(mesh, new Texture("resources/images/Ai Sasha.png"));
        //Model square = new Model(mesh, new Texture("resources/images/Ai Sasha.png"));
        Entity entity = square.createEntity();
        entity.setPosition(0, 0, -10f);
        scene.add(entity);
        
        
        //scene.getTextStrips().add(new TextStrip("Anthony", 10, new Vector3(100, 100, 100), new Vector2(100, 200), font));
        
        Canvas background = new Canvas(width, height, new Color(145, 139, 80));
        scene.add(background);

        //320
        int fontSize = 750;
        
        font = new Font("resources/fonts/Bagnard.otf");
        System.out.println(font.getFontData().cffPrivateDict);
        CanvasAtlas letter = (CanvasAtlas) FontAtlasGenerator.getFilledAtlas(fontSize, font);
        Canvas canvas2 = new Canvas(letter);
        scene.add(canvas2);
    
        //CanvasAtlas letterOutline = (CanvasAtlas) FontAtlasGenerator.getOutlinedAtlas(fontSize, font);
        //Canvas canvas3 = new Canvas(letterOutline);
        //scene.add(canvas3);
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
    
    
    public Scene getScene()
    {
        return scene;
    }
}
