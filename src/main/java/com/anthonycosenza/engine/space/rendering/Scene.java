package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.loader.text.TextStrip;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private List<Entity> entities;
    private List<Canvas> canvasItems;
    private List<TextStrip> textStrips;
    private Camera camera;
    
    
    public Scene()
    {
        entities = new ArrayList<>();
        camera = new Camera();
        textStrips = new ArrayList<>();
        canvasItems = new ArrayList<>();
    }
    
    public Camera getCamera()
    {
        return camera;
    }
    
    public List<Entity> getEntities()
    {
        return entities;
    }
    
    public List<Canvas> getCanvasItems()
    {
        return canvasItems;
    }
    
    public List<TextStrip> getTextStrips()
    {
        return textStrips;
    }
    
    public void add(Entity entity)
    {
        entities.add(entity);
    }
    
    public void add(Canvas canvas)
    {
        canvasItems.add(canvas);
    }
    
}
