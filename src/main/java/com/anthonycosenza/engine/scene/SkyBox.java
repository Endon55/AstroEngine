package com.anthonycosenza.engine.scene;

import com.anthonycosenza.engine.render.Model;
import com.anthonycosenza.engine.render.ModelLoader;
import com.anthonycosenza.engine.render.TextureCache;

public class SkyBox
{
    private Entity skyBoxEntity;
    private Model skyBoxModel;
    
    public SkyBox(String skyBoxModelPath, TextureCache textureCache)
    {
        skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache);
        skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel.getId());
    }
    
    public Entity getSkyBoxEntity()
    {
        return skyBoxEntity;
    }
    
    public Model getSkyBoxModel()
    {
        return skyBoxModel;
    }
    
    public void setScale(float scale)
    {
        getSkyBoxEntity().setScale(scale);
    }
}
