package com.anthonycosenza.engine.render.light;

import org.joml.Vector3f;

public class PointLight
{
    private Attenuation attenuation;
    private Vector3f color;
    private Vector3f position;
    private float intensity;
    
    
    public PointLight(Vector3f color, Vector3f position, float intensity)
    {
        attenuation = new Attenuation(0, 0, 1);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }
    
    public Attenuation getAttenuation()
    {
        return attenuation;
    }
    
    public Vector3f getColor()
    {
        return color;
    }
    
    public float getIntensity()
    {
        return intensity;
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
    
    public void setAttenuation(Attenuation attenuation)
    {
        this.attenuation = attenuation;
    }
    
    public void setColor(Vector3f color)
    {
        this.color = color;
    }
    
    public void setColor(float r, float g, float b)
    {
        color.set(r, g, b);
    }
    
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }
    
    public void setPosition(float x, float y, float z)
    {
        position.set(x, y, z);
    }
    
    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }
}

