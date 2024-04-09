package com.anthonycosenza.engine.render.light;

public class Attenuation
{
    private float constant;
    private float exponent;
    private float linear;
    
    public Attenuation(float constant, float linear, float exponent)
    {
        this.constant = constant;
        this.exponent = exponent;
        this.linear = linear;
    }
    
    public float getConstant()
    {
        return constant;
    }
    
    public void setConstant(float constant)
    {
        this.constant = constant;
    }
    
    public float getExponent()
    {
        return exponent;
    }
    
    public void setExponent(float exponent)
    {
        this.exponent = exponent;
    }
    
    public float getLinear()
    {
        return linear;
    }
    
    public void setLinear(float linear)
    {
        this.linear = linear;
    }
}
