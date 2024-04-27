package com.anthonycosenza;


public class Model
{
    private Mesh mesh;
    
    public Model(Mesh mesh)
    {
        this.mesh = mesh;
    }
    
    public Mesh getMesh()
    {
        return mesh;
    }
    
    public Entity createEntity()
    {
        return new Entity(this);
    }
    
}
