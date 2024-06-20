package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Positional;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Node3D extends Node implements Positional
{
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;
    
    private transient final Matrix4f matrix;
    
    public Node3D()
    {
        matrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1, 1, 1);
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
    
    /*
     * Turning in place. Seeing 360 around you. Equivalent to rotateY.
     */
    public void yaw(float angle)
    {
        rotateY(angle);
    }
    
    /*
     * Not moving, but seeing your camera rotate the scene 360. Equivalent to rotateZ.
     */
    public void roll(float angle)
    {
        rotateZ(angle);
    }
    /*
     * Front flip. Equivalent to rotateX.
     */
    public void pitch(float angle)
    {
        rotateX(angle);
    }
    
    public void rotateX(float angle)
    {
        this.rotation.rotateX(angle);
    }
    
    public void rotateY(float angle)
    {
        this.rotation.rotateY(angle);
    }
    
    public void rotateZ(float angle)
    {
        this.rotation.rotateZ(angle);
    }
    public void setRotationDeg(float x, float y, float z, float angle)
    {
        this.rotation.fromAxisAngleDeg(x, y, z, angle);
    }
    
    public void setRotationRad(float x, float y, float z, float angle)
    {
        this.rotation.fromAxisAngleRad(x, y, z, angle);
    }
    
    public void setScale(Vector3f scale)
    {
        setScale(scale.x, scale.y, scale.z);
    }
    public void setScale(float x, float y, float z)
    {
        this.scale.set(x, y, z);
        updateMatrix();
    }
    
    public Vector3f getScale()
    {
        return scale;
    }
    
    public void setPosition(float x, float y, float z)
    {
        this.position.set(x, y , z);
        //updateMatrix();
    }
    public Vector3f getEulerAngle()
    {
        return this.rotation.getEulerAnglesXYZ(new Vector3f());
    }
    
    public Matrix4f getTransformation()
    {
        //This is expensive, find a better way to do this. Maybe convert the serializer to use getters and setters instead.
        updateMatrix();
        return this.matrix;
    }
    
    public void updateMatrix()
    {
        this.matrix.translationRotateScale(position, rotation, scale);
    }
}
