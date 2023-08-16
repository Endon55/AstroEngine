package com.anthonycosenza.engine.scene;

import com.anthonycosenza.engine.game.Projection;
import com.anthonycosenza.engine.render.Mesh;
import com.anthonycosenza.engine.render.Model;

import java.util.HashMap;
import java.util.Map;

public class Scene
{
    private final Map<String, Model> modelMap;
    private Projection projection;
    
    public Scene(int width, int height)
    {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
    }
    public void addEntity(Entity entity)
    {
        String modelID = entity.getModelID();
        Model model = modelMap.get(modelID);
        if(model == null) throw new RuntimeException("Could not find model: " + modelID);
        model.getEntityList().add(entity);
    }
    public void addModel(String meshID, Model model)
    {
        modelMap.put(meshID, model);
    }
    public Map<String, Model> getModelMap()
    {
        return modelMap;
    }

    public Projection getProjection()
    {
        return projection;
    }
    
    
    public void cleanup()
    {
        modelMap.values().forEach(Model::cleanup);
    }
    
    
    
}
