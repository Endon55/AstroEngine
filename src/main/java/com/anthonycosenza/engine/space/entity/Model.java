package com.anthonycosenza.engine.space.entity;


public class Model
{
    private Mesh mesh;
    private Texture texture;
    
    public Model(Mesh mesh)
    {
        this.mesh = mesh;
        this.texture = null;
    }
    
    public Model(Mesh mesh, Texture texture)
    {
        this.mesh = mesh;
        this.texture = texture;
    }
    
    public Mesh getMesh()
    {
        return mesh;
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
