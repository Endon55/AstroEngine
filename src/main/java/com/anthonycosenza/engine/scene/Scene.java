package com.anthonycosenza.engine.scene;

import com.anthonycosenza.engine.render.Model;
import com.anthonycosenza.engine.render.TextureCache;
import com.anthonycosenza.engine.render.gui.IGuiInstance;
import com.anthonycosenza.engine.render.light.SceneLighting;

import java.util.HashMap;
import java.util.Map;

public class Scene
{
    private Camera camera;
    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private IGuiInstance guiInstance;
    private SkyBox skyBox;
    

    
    private SceneLighting sceneLighting;
    
    
    public Scene(int width, int height)
    {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
    }
    public void addEntity(Entity entity)
    {
        String modelID = entity.getModelID();
        Model model = modelMap.get(modelID);
        if(model == null) throw new RuntimeException("Could not find model: " + modelID);
        model.getEntityList().add(entity);
    }
    public void addModel(Model model)
    {
        modelMap.put(model.getId(), model);
    }
    public Map<String, Model> getModelMap()
    {
        return modelMap;
    }

    public Projection getProjection()
    {
        return projection;
    }
    
    public TextureCache getTextureCache()
    {
        return textureCache;
    }
    
    public Camera getCamera()
    {
        return camera;
    }
    
    public void resize(int width, int height)
    {
        projection.updateProjMatrix(width, height);
    }
    
    public void cleanup()
    {
        modelMap.values().forEach(Model::cleanup);
    }
    
    public IGuiInstance getGuiInstance()
    {
        return guiInstance;
    }
    
    public void setGuiInstance(IGuiInstance guiInstance)
    {
        this.guiInstance = guiInstance;
    }
    
    public SceneLighting getSceneLighting()
    {
        return sceneLighting;
    }
    
    public void setSceneLighting(SceneLighting sceneLighting)
    {
        this.sceneLighting = sceneLighting;
    }
    
    public SkyBox getSkyBox()
    {
        return skyBox;
    }
    
    public void setSkyBox(SkyBox skyBox)
    {
        this.skyBox = skyBox;
    }
}
