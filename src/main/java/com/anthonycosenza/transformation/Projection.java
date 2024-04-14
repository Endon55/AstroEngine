package com.anthonycosenza.transformation;

import org.joml.Matrix4f;

public class Projection
{
    private float fov;
    private float aspectRatio;
    private float zNear;
    private float zFar;
    private Matrix4f projectionMatrix;
    
    /**
     *
     * @param fovDegrees how wide the camera lens is and how much of the world it captures. Bigger captures more objects but makes everything smaller.
     */
    public Projection(float fovDegrees, int width, int height, float zNear, float zFar)
    {
        this.fov = (float) Math.toRadians(fovDegrees);
        this.aspectRatio = (float) width / height;
        this.zNear = zNear;
        this.zFar = zFar;
        projectionMatrix = new Matrix4f();
        
        updateMatrix();
    }
    
    public void resize(int width, int height)
    {
        aspectRatio = (float) height / (float) width;
        updateMatrix();
    }
    public void zDistance(float zNear, float zFar)
    {
        this.zNear = zNear;
        this.zFar = zFar;
        updateMatrix();
    }
    public float getFov()
    {
        return fov;
    }
    
    public void setFov(float fovDegrees)
    {
        this.fov = (float) Math.toRadians(fovDegrees);
    }
    
    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }
    
    private void updateMatrix()
    {
        /*
            The goal here is to populate the 4x4 matrix with values that when multiplied against position vectors will result in a
            new vectors that are scaled such that further away objects appear smaller.
            
            We need to scale each part of the incoming vector a little differently.
            X is scaled by the Aspect ratio of the monitor and the field of view(fov)
            Y is scaled by just the field of view
            Z is scaled by the ratio of zNear to zFar - that ratio * zNear
         */
        //This is the basic idea, however joml Matrix4f uses a slightly different implementation under the hood.
        // aspect * 1/tan(fov/2)                0                0                          0
        //                     0     1/tan(fov/2)                0                          0
        //                     0                0     zf/(zf - zn)     (-zf * zn) / (zf - zn)
        //                     0                0                1                          0
        
        projectionMatrix.setPerspective(fov, aspectRatio, zNear, zFar);
    }
}
