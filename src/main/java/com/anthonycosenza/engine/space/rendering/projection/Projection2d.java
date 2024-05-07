package com.anthonycosenza.engine.space.rendering.projection;

import com.anthonycosenza.engine.util.math.matrix.Matrix4;

public class Projection2d
{
    private float aspectRatio;
    private float width;
    private float height;
    private Matrix4 projectionMatrix;

    public Projection2d(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.aspectRatio = (float) width / height;
        projectionMatrix = new Matrix4();
        
        updateMatrix();
    }
    
    public void resize(int width, int height)
    {
        aspectRatio = (float) width / (float) height;
        updateMatrix();
    }

    public Matrix4 getMatrix()
    {
        return projectionMatrix;
    }
    
    private void updateMatrix()
    {
        
        projectionMatrix.identity()
                //.m00(2 / width)
                .m11(1 / aspectRatio)
                .m33(0);
    }
    
}
