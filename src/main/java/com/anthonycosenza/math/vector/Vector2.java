package com.anthonycosenza.math.vector;

public class Vector2
{
    private float x;
    private float y;
    
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector2 set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public float x()
    {
        return x;
    }
    
    public float y()
    {
        return y;
    }
}
