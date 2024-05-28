package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.annotations.Ignore;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Positional;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Node3D extends Node implements Positional
{
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;
    @Ignore
    private final Matrix4f matrix;
    
    public Node3D()
    {
        matrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1, 1, 1);
    }
    
    
    public void setPosition(float x, float y, float z)
    {
        this.position.set(x, y , z);
        //updateMatrix();
    }
    
    public Matrix4f getTransformation()
    {
        //This is expensive, find a better way to do this. Maybe convert the serializer to use getters and setters instead.
        updateMatrix();
        return matrix;
    }
    
    public void updateMatrix()
    {
        matrix.translationRotateScale(position, rotation, scale);
    }
}
