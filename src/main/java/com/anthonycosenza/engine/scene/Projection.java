package com.anthonycosenza.engine.scene;

import org.joml.Matrix4f;

public class Projection
{
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    
    private Matrix4f projectionMatrix;
    private Matrix4f invProjMatrix;
    
    public Projection(int width, int height)
    {
        projectionMatrix = new Matrix4f();
        invProjMatrix = new Matrix4f();
        updateProjMatrix(width, height);
    }
    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }
    
    public Matrix4f getInvProjMatrix()
    {
        return invProjMatrix;
    }
    
    public void updateProjMatrix(int width, int height)
    {
        projectionMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
        invProjMatrix.set(projectionMatrix).invert();
    }
}
