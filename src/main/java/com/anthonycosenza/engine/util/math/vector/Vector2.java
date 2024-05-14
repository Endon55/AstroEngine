package com.anthonycosenza.engine.util.math.vector;

import java.math.BigDecimal;
import java.math.MathContext;

public class Vector2
{
    private float x;
    private float y;
    
    public Vector2()
    {
        this.x = 0;
        this.y = 0;
    }
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector2(Vector2 vector)
    {
        this.x = vector.x();
        this.y = vector.y();
    }
    
    public Vector2 mult(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    public Vector2 set(Vector2 vector)
    {
        return this.set(vector.x(), vector.y());
    }
    
    public Vector2 normalize()
    {   //                 _______________
        // scale factor = √x^2 + y^2 + z^2
        // so we divide all the values in vector by that scalar.
        return this.mult(1 / (float) Math.sqrt(Math.fma(x(), x(), y() * y())));
    }
    public Vector2 set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector2 add(Vector2 vector)
    {
        this.x += vector.x();
        this.y += vector.y();
        return this;
    }
    
    public Vector2 add(float x, float y)
    {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vector2 add(int x, int y, Vector2 destination)
    {
        destination.x = this.x + x;
        destination.y = this.y + y;
        return destination;
    }
    public Vector2 addX(float x)
    {
        this.x += x;
        return this;
    }
    
    public Vector2 addY(float y)
    {
        this.y += y;
        return this;
    }
    
    public Vector2 subtract(Vector2 vector)
    {
        this.x -= vector.x();
        this.y -= vector.y();
        return this;
    }
    
    
    public Vector2 roundX(float tolerance)
    {
        int x = (int) x();
        if(Math.abs(x() - x) < tolerance)
        {
            x(x);
            return this;
        }
        x = x < 0 ? x - 1 : x + 1;
        if(Math.abs(x() - x)  <= tolerance) x(x);
        return this;
    }
    
    public Vector2i getVector2i()
    {
        return new Vector2i(Math.round(x()), Math.round(y()));
    }
    
    public Vector2 roundY(float tolerance)
    {
        int y = (int) y();
        if(Math.abs(y() - y) < tolerance) y(y);
        y++;
        if(Math.abs(y() - y) < tolerance) y(y);
        return this;
    }
    
    public Vector2 round(float tolerance)
    {
        roundX(tolerance);
        return roundY(tolerance);
    }

    public Vector2 round()
    {
        x(Math.round(x()));
        y(Math.round(y()));
        return this;
    }
    
    public Vector2 subtract(Vector2 subtract, Vector2 destination)
    {
        destination.x = this.x - subtract.x();
        destination.y = this.y - subtract.y();
        return destination;
    }
    
    public float x()
    {
        return x;
    }
    
    public float y()
    {
        return y;
    }
    
    public void x(float x)
    {
        this.x = x;
    }
    
    public void y(float y)
    {
        this.y = y;
    }
    
    
    @Override
    public String toString()
    {
        return "[" + x + ", " + y + "]";
    }
    
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Vector2 vector2 = (Vector2) o;
        
        if(Float.compare(vector2.x, x) != 0) return false;
        return Float.compare(vector2.y, y) == 0;
    }
    
    @Override
    public int hashCode()
    {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}
