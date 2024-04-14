package com.anthonycosenza;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private List<Entity> entities;
    
    public Scene()
    {
        entities = new ArrayList<>();

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
