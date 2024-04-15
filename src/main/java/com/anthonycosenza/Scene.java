package com.anthonycosenza;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private List<Entity> entities;
    private Camera camera;
    
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
    
    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }
}
