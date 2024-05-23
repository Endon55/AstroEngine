package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.space.entity.EntityInstance;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    private final List<EntityInstance> entities;
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
    
    public List<EntityInstance> getInstances()
    {
        return entities;
    }
    
    public void add(EntityInstance instance)
    {
        entities.add(instance);
    }
    
    
}
