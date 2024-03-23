package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.scene.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model
{
    private final String id;
    //List of all entities created using this model
    private List<Entity> entityList;
    //List of all materials to apply to this model
    private List<Material> materialList;
    
    public Model(String id, List<Material> materialList)
    {
        this.id = id;
        this.materialList = materialList;
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
    
    public List<Material> getMaterialList()
    {
        return materialList;
    }
    
    public void cleanup()
    {
        materialList.forEach(Material::cleanup);
    }
    
}
