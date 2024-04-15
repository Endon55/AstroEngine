package com.anthonycosenza.math.matrix;

public class Matrix2 implements IMatrix
{
    private float m00, m01;
    private float m10, m11;
    
    public Matrix2(float fillValue)
    {
        m00 = fillValue;
        m01 = fillValue;
        
        m10 = fillValue;
        m11 = fillValue;
    }
    
    public Matrix2(float m00, float m01, float m10, float m11)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }
    
    public void m00(float m00)
    {
        this.m00 = m00;
    }
    
    public void m01(float m01)
    {
        this.m01 = m01;
    }
    
    public void m10(float m10)
    {
        this.m10 = m10;
    }
    
    public void m11(float m11)
    {
        this.m11 = m11;
    }
    
    public float m00()
    {
        return m00;
    }
    
    public float m01()
    {
        return m01;
    }
    
    public float m10()
    {
        return m10;
    }
    
    public float m11()
    {
        return m11;
    }
    
    public Matrix2 mult(Matrix2 matrix)
    {
        float n00 = m00 * matrix.m00() + m01 * matrix.m10();
        float n01 = m00 * matrix.m01() + m01 * matrix.m11();
        float n10 = m10 * matrix.m00() + m11 * matrix.m10();
        float n11 = m10 * matrix.m01() + m11 * matrix.m11();
        
        m00 = n00;
        m01 = n01;
        m10 = n10;
        m11 = n11;
        
        return this;
    }
    
    @Override
    public int getRows()
    {
        return 2;
    }
    
    @Override
    public int getColumns()
    {
        return 2;
    }
    
    @Override
    public String toString()
    {
        return "{[" + m00() + ", " + m01() + "]" + "\n [" +
                m10() + ", " + m11() + "]}";
    }
}
