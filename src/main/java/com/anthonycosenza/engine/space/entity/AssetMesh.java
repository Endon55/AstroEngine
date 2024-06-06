package com.anthonycosenza.engine.space.entity;

public class AssetMesh extends Mesh
{
    public AssetMesh(float[] vertices, int[] indices, float[] textureCoordinates)
    {
        super(vertices, indices, textureCoordinates);
    }
    
    @Override
    public void initialize()
    {
        //We call the constructor which sets for us.
    }

}
