package com.anthonycosenza.engine.space.node._2d;

import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Positional;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Node2D extends Node implements Positional
{
    public Vector2f position;
    
    @Override
    public Matrix4f getTransformation()
    {
        return null;
    }
}
