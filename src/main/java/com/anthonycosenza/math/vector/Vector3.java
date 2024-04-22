package com.anthonycosenza.math.vector;

public class Vector3
{
    public float x;
    public float y;
    public float z;
    
    public Vector3()
    {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }
    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3 copy(Vector3 destination)
    {
        destination.set(this.x, this.y, this.z);
        return this;
    }
    public Vector3 add(Vector3 vector)
    {
        return this.add(vector.x(), vector.y(), vector.z());
    }
    
    public Vector3 add(float x, float y, float z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    
    public Vector3 getUnit()
    {
        float magnitude = (float) Math.sqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
        Vector3 unit = new Vector3();
        this.copy(unit);
        
        return unit.divide(magnitude);
    }
    
    public boolean isEmpty()
    {
        return x == 0 && y == 0 && z == 0;
    }
    
    public Vector3 divide(float scalar)
    {
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }
    
    public float mult(Vector3 vector)
    {
        return Math.fma(x(), vector.x(), Math.fma(y(), vector.y(), z() * vector.z()));
    }
    
    public Vector3 mult(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }
    
    public Vector3 invert()
    {
        return set(-x, -y, -z);
    }
    public Vector3 normalize()
    {   //                 _______________
        // scale factor = √x^2 + y^2 + z^2
        // so we divide all the values in vector by that scalar.
        return this.mult(1 / (float)Math.sqrt(Math.fma(x(), x(), Math.fma(y(), y(), z() * z()))));
    }
    
    public Vector3 set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
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
    
    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
