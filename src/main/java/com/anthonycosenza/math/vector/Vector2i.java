package com.anthonycosenza.math.vector;

public class Vector2i
{
    private int x;
    private int y;
    
    public Vector2i()
    {
        this.x = 0;
        this.y = 0;
    }
    public Vector2i(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector2i(Vector2i vector)
    {
        this.x = vector.x();
        this.y = vector.y();
    }
    
    public Vector2i mult(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    public Vector2i set(Vector2i vector)
    {
        return this.set(vector.x(), vector.y());
    }
    
    public Vector2i normalize()
    {   //                 _______________
        // scale factor = √x^2 + y^2 + z^2
        // so we divide all the values in vector by that scalar.
        return this.mult(1 / (float) Math.sqrt(Math.fma(x(), x(), y() * y())));
    }
    public Vector2i set(int x, int y)
    {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector2i add(Vector2i vector)
    {
        this.x += vector.x();
        this.y += vector.y();
        return this;
    }
    
    public Vector2i add(int x, int y)
    {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vector2i addX(int x)
    {
        this.x += x;
        return this;
    }
    
    public Vector2i addY(int y)
    {
        this.y += y;
        return this;
    }
    
    public Vector2i subtract(Vector2i vector)
    {
        this.x -= vector.x();
        this.y -= vector.y();
        return this;
    }

    
    public Vector2i subtract(Vector2i subtract, Vector2i destination)
    {
        destination.x = this.x - subtract.x();
        destination.y = this.y - subtract.y();
        return destination;
    }
    
    public int x()
    {
        return x;
    }
    
    public int y()
    {
        return y;
    }
    
    public void x(int x)
    {
        this.x = x;
    }
    
    public void y(int y)
    {
        this.y = y;
    }
    
    
    @Override
    public String toString()
    {
        return "[" + x + ", " + y + "]";
    }
}
