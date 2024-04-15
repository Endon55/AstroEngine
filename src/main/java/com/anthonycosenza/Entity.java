package com.anthonycosenza;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity
{
    private Model model;
    private Matrix4f entityMatrix;
    private Vector3f position;
    //We use a quaternion to store rotation as it prevents "Gimbal Lock" essentially if 2 axis have the same value they get locked into place and the math can't break them out.
    //Quaternions are like 4d imaginary numbers, for some reason that's well beyond my paygrade it makes it much easier to track rotation with quats.
    private Quaternionf rotation;
    float scale;
    
    public Entity(Model model)
    {
        this.model = model;
        entityMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1f;
        updateMatrix();
    }
    
    public void setPosition(Vector3f position)
    {
        this.position = position;
        updateMatrix();
    }
    
    public void setPosition(float x, float y, float z)
    {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        updateMatrix();
    }
    public void rotate(float x, float y, float z, float angle)
    {
        rotation.fromAxisAngleDeg(x, y, z, angle);
        updateMatrix();
    }
    
    public Model getModel()
    {
        return model;
    }
    
    public Matrix4f getMatrix()
    {
        return entityMatrix;
    }
    
    
    public void updateMatrix()
    {
        entityMatrix.translationRotateScale(position, rotation, scale);
        System.out.println(entityMatrix.properties());
    }
}
