package com.anthonycosenza.engine.space.shape;

import com.anthonycosenza.engine.space.entity.Mesh;

public class ShapeBuilder
{
    
    public static Mesh square(int width, int height)
    {
        width /= 2;
        height /= 2;
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
        return new Mesh(vertices, indices, texture);
    }
    
    public static float[] pyramid3(float height, float baseEdgeLength)
    {
        float hHeight = height * .5f;
        float hBEL = baseEdgeLength * .5f;
        float width = (float)Math.sqrt(baseEdgeLength * baseEdgeLength - hBEL * hBEL);
        float hWidth = width * .5f;
        
/*        return new float[]{
                //Top Point
                0, 0, hHeight,
                //Bottom Left
                -hBEL, -hWidth, -hHeight,
                //Bottom Right
                hBEL, -hWidth, -hHeight,
                //Bottom Top
                0, -hWidth, hHeight
        };*/
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
    }
}
