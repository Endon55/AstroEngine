package com.anthonycosenza.engine.game;

import com.anthonycosenza.engine.render.Mesh;

import java.util.HashMap;
import java.util.Map;

public class Scene
{
    private final Map<String, Mesh> meshMap;
    public Scene()
    {
        meshMap = new HashMap<>();
    }
    
    public void addMesh(String meshID, Mesh mesh)
    {
        meshMap.put(meshID, mesh);
    }
    public Map<String, Mesh> getMeshMap()
    {
        return meshMap;
    }
    public void cleanup()
    {
        meshMap.values().forEach(Mesh::cleanup);
    }
}
