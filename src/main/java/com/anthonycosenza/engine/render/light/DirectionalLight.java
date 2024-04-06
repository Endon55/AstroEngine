package com.anthonycosenza.engine.render.light;

import org.joml.Vector3f;

public class DirectionalLight
{
    private Vector3f color;
    private Vector3f direction;
    private float intensity;
    
    public DirectionalLight(Vector3f color, Vector3f direction, float intensity)
    {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    
    public Vector3f getColor()
    {
        return color;
    }
    
    public float getIntensity()
    {
        return intensity;
    }
    
    public Vector3f getDirection()
    {
        return direction;
    }
    
    public void setColor(Vector3f color)
    {
        this.color = color;
    }
    
    public void setColor(float r, float g, float b)
    {
        color.set(r, g, b);
    }
    
    public void setDirection(Vector3f position)
    {
        this.direction = position;
    }
    
    public void setDirection(float x, float y, float z)
    {
        direction.set(x, y, z);
    }
    
    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }
}
