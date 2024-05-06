package com.anthonycosenza.engine.space.shape;

import com.anthonycosenza.engine.space.entity.Mesh;

public class Pyramid3 extends Mesh
{
    
    public Pyramid3(float height, float baseEdgeLength)
    {
        //bottom points y dist from center =
        super(ShapeBuilder.pyramid3(height, baseEdgeLength),
                new int[]
                {
                    0, 1, 2,
                    0, 2, 3,
                    0, 3, 1,
                    1, 3, 2
                },new float[]
                {
                     0, 0, 1,
                     0, 1, 1,
                     1, 0, 0,
                     1, 1, 0
                });
    }
}
