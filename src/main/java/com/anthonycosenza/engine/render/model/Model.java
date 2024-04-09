package com.anthonycosenza.engine.render.model;

import com.anthonycosenza.engine.render.model.animation.Animation;
import com.anthonycosenza.engine.scene.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model
{
    private final String id;
    //List of all materials to apply to this model
    private List<Material> materialList;
    private List<Animation> animationList;
    
    //List of all entities created using this model
    private List<Entity> entityList;
    
    public Model(String id, List<Material> materialList, List<Animation> animationList)
    {
        this.id = id;
        this.materialList = materialList;
        this.animationList = animationList;
        
        entityList = new ArrayList<>();
    }
    
    public String getId()
    {
        return id;
    }
    
    public List<Animation> getAnimationList()
    {
        return animationList;
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
