package com.anthonycosenza.engine.space.entity;


import com.anthonycosenza.engine.space.entity.texture.Material;
import com.anthonycosenza.engine.space.entity.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Model
{
    private List<Material> materials;
    private List<Mesh> meshes;
    private Texture texture;
    
    public Model(List<Material> materials)
    {
        this.materials = materials;
        this.texture = null;
    }
    
    
    public Model(List<Mesh> meshes, Texture texture)
    {
        this.meshes = new ArrayList<>(meshes);
        this.texture = texture;
    }
    public Model(Mesh mesh)
    {
        this.meshes = new ArrayList<>();
        this.texture = null;
    }
    
    public Model(Mesh mesh, Texture texture)
    {
        this.meshes = new ArrayList<>();
        this.texture = texture;
    }
    
    public List<Material> getMaterials()
    {
        return materials;
    }
    
    public List<Mesh> getMeshes()
    {
        return meshes;
    }
    
    public Texture getTexture()
    {
        return texture;
    }
    
    public Entity createEntity()
    {
        return new Entity(this);
    }
    
}
