package com.anthonycosenza.projection;

import com.anthonycosenza.math.matrix.Matrix4;

public class Projection2d
{
    private float aspectRatio;
    private Matrix4 projectionMatrix;

    public Projection2d(int width, int height)
    {
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
        projectionMatrix.identity().m11(1 / aspectRatio);
    }
    
}
