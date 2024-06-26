package com.anthonycosenza.engine.util.math;

import imgui.ImColor;

public class Color
{
    private float r;
    private float g;
    private float b;
    private float a;
    
    public Color()
    {
        this.r = r / 255f;
        this.g = g / 255f;
        this.b = b / 255f;
        this.a = 1;
    }
    public Color(Color color)
    {
        this(color.r(), color.g(), color.b(), color.a());
    }
    public Color(int r, int g, int b)
    {
        this.r = r / 255f;
        this.g = g / 255f;
        this.b = b / 255f;
        this.a = 1;
    }
    public Color(int r, int g, int b, int a)
    {
        this.r = r / 255f;
        this.g = g / 255f;
        this.b = b / 255f;
        this.a = a / 255f;
    }
    
    public Color(float r, float g, float b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1;
    }
    public Color(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public Color(float[] floats)
    {
         this(floats[0], floats[1], floats[2], floats[3]);
    }
    
    public Color add(int r, int g, int b, int a)
    {
        return add(r / 255f, g / 255f, b / 255f, a / 255f);
    }
    
    public Color add(float r, float g, float b, float a)
    {
        this.r += r;
        this.g += g;
        this.b += b;
        this.a += a;
        
        return this;
    }
    public Color mult(float scalar)
    {
        this.r *= scalar;
        this.g *= scalar;
        this.b *= scalar;
        this.a *= scalar;
        
        return this;
    }
    
    public Color normalize()
    {
        this.r %= 1f;
        this.g %= 1f;
        this.b %= 1f;
        this.a %= 1f;
        
        return this;
    }
    
    public Color set(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }
    
    
    public float r()
    {
        return r;
    }
    
    public float g()
    {
        return g;
    }
    
    public float b()
    {
        return b;
    }
    
    public float a()
    {
        return a;
    }
    
    public Color r(float r)
    {
        this.r = r;
        return this;
    }
    
    public Color g(float g)
    {
        this.g = g;
        return this;
    }
    
    public Color b(float b)
    {
        this.b = b;
        return this;
    }
    
    public Color a(float a)
    {
        this.a = a;
        return this;
    }
    
    public Color r(int r)
    {
        this.r = r / 255f;
        return this;
    }
    
    public Color g(int g)
    {
        this.g = g / 255f;
        return this;
    }
    
    public Color b(int b)
    {
        this.b = b / 255f;
        return this;
    }
    
    public Color a(int a)
    {
        this.a = a / 255f;
        return this;
    }
    
    public int getInt()
    {
        return ImColor.rgba(r, g, b, a);
    }
    
    public float[] getAsArray()
    {
        return new float[]{r(), g(), b(), a()};
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Color color = (Color) o;
        
        if(Float.compare(color.r, r) != 0) return false;
        if(Float.compare(color.g, g) != 0) return false;
        if(Float.compare(color.b, b) != 0) return false;
        return Float.compare(color.a, a) == 0;
    }
    
    @Override
    public int hashCode()
    {
        int result = (r != +0.0f ? Float.floatToIntBits(r) : 0);
        result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
        result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
        result = 31 * result + (a != +0.0f ? Float.floatToIntBits(a) : 0);
        return result;
    }
    
    @Override
    public String toString()
    {
        return "Color(" + r + ","  + g + "," + b + "," + a + ")";
    }
}
