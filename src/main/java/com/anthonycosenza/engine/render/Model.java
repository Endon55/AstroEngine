package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.scene.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model
{
    private final String id;
    private List<Entity> entityList;
    private List<Mesh> meshList;
    
    public Model(String id, List<Mesh> meshList)
    {
        this.id = id;
        this.meshList = meshList;
        entityList = new ArrayList<>();
    }
    
    public String getId()
    {
        return id;
    }
    
    public List<Entity> getEntityList()
    {
        return entityList;
    }
    
    public List<Mesh> getMeshList()
    {
        return meshList;
    }
    
    public void cleanup()
    {
        meshList.forEach(Mesh::cleanup);
    }
    
}
