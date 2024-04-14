package com.anthonycosenza;

import org.joml.Matrix4f;

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
