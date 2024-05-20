package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private final List<Entity> entities;
    private final Camera camera;
    
    
    public Scene()
    {
        entities = new ArrayList<>();
        camera = new Camera();
    }
    
    public Camera getCamera()
    {
        return camera;
    }
    
    public List<Entity> getEntities()
    {
        return entities;
    }
    
    public void add(Entity entity)
    {
        entities.add(entity);
    }
    
    
}
