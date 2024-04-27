package com.anthonycosenza.math.vector;

public class Vector4
{
    private float x;
    private float y;
    private float z;
    private float w;
    
    public Vector4(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4 set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }
    
    public Vector4 mult(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
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
    
    public float z()
    {
        return z;
    }
    
    public float w()
    {
        return w;
    }
    
    
    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + ", " + w + "]";
    }
}