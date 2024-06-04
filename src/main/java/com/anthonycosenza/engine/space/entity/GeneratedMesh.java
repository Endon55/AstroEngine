package com.anthonycosenza.engine.space.entity;

import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.space.shape.MeshShape;

public class GeneratedMesh extends Mesh
{
    @Property
    public MeshShape shape;
    @Property
    public float width;
    @Property
    public float height;
    @Property
    public float depth;
    @Property
    public int subdivisions;
    
    public GeneratedMesh()
    {
    
    }
    public void generate()
    {
        switch(shape)
        {
            case PLANE -> generatePlane();
        }
        //generate all the values
        //set(vertices, indices, textureCoordinates);
    }
    private void generatePlane()
    {
        float width = this.width /= 2f;
        float depth = this.depth /= 2f;
        float[] vertices = new float[]
                {       //X, Y, Z
                        -width, 0, -depth,
                        -width, 0, depth,
                        width, 0, -depth,
                        width, 0, depth
                };
        int[] indices = new int[]
                {
                        0, 1, 2,
                        2, 1, 3
                };
        float[] texture = new float[]
                {       //X, Y
                        0, 1,
                        0, 0,
                        1, 1,
                        1, 0
                };
        set(vertices, indices, texture);
    }
    
    public void square(float width, float height)
    {
        width /= 2f;
        height /= 2f;
        float[] vertices = new float[]
                {       //X, Y, Z
                        -width, -height, 0,
                        -width, height, 0,
                        width, -height, 0,
                        width, height, 0
                };
        int[] indices = new int[]
                {
                        0, 2, 1,
                        2, 3, 1
                };
        float[] texture = new float[]
                {       //X, Y
                        0, 1,
                        0, 0,
                        1, 1,
                        1, 0
                };
        set(vertices, indices, texture);
    }
    
    /*public void pyramid3(float height, float baseEdgeLength)
    {
        float hHeight = height * .5f;
        float hBEL = baseEdgeLength * .5f;
        float width = (float) Math.sqrt(baseEdgeLength * baseEdgeLength - hBEL * hBEL);
        float hWidth = width * .5f;
        
*//*        return new float[]{
                //Top Point
                0, 0, hHeight,
                //Bottom Left
                -hBEL, -hWidth, -hHeight,
                //Bottom Right
                hBEL, -hWidth, -hHeight,
                //Bottom Top
                0, -hWidth, hHeight
        };*//*
        return new float[]{
                //Top Point
                0, hHeight, 0,
                //Bottom Left
                -hBEL, -hHeight, -hWidth,
                //Bottom Right
                hBEL, -hHeight, -hWidth,
                //Bottom Top
                0, -hHeight, hWidth
        };
    }*/
}
