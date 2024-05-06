package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.loader.text.TextStrip;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private List<Entity> entities;
    private List<TextStrip> textStrips;
    private Camera camera;
    
    public Scene()
    {
        entities = new ArrayList<>();
        camera = new Camera();
        textStrips = new ArrayList<>();
    }
    
    public Camera getCamera()
    {
        return camera;
    }
    
    public List<Entity> getEntities()
    {
        return entities;
    }
    
    public List<TextStrip> getTextStrips()
    {
        return textStrips;
    }
    
    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }
    
}
