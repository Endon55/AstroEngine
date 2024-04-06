package com.anthonycosenza.engine.scene;

import com.anthonycosenza.engine.Constants;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity
{
    private final String id;
    private final String modelID;
    private Matrix4f modelMatrix;
    private Vector3f position;
    private Quaternionf rotation;
    private float scale;
    
    public Entity(String id, String modelID)
    {
        this.id = id;
        this.modelID = modelID;
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1;
        updateModelMatrix();
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getModelID()
    {
        return modelID;
    }
    
    public Matrix4f getModelMatrix()
    {
        return modelMatrix;
    }
    
    public void setModelMatrix(Matrix4f modelMatrix)
    {
        this.modelMatrix = modelMatrix;
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
    
    public Quaternionf getRotation()
    {
        return rotation;
    }
    
    public float getScale()
    {
        return scale;
    }
    
    public void setPosition(float x, float y, float z)
    {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    
    public void setRotation(float x, float y, float z, float angle)
    {
        this.rotation.fromAxisAngleRad(x, y, z, angle % Constants.MAX_RADIANS);
    }
    
    public void setScale(float scale)
    {
        this.scale = scale;
        updateModelMatrix();
    }
    
    public void updateModelMatrix()
    {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }
}
