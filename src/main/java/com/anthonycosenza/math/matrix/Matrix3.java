package com.anthonycosenza.math.matrix;

public class Matrix3 implements IMatrix
{
    private float m00, m01, m02;
    private float m10, m11, m12;
    private float m20, m21, m22;
    
    public Matrix3(float fillValue)
    {
        fill(fillValue);
    }
    
    public Matrix3(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }
    
    public Matrix3 fill(float value)
    {
        m00 = value;
        m01 = value;
        m02 = value;
        
        m10 = value;
        m11 = value;
        m12 = value;
        
        m20 = value;
        m21 = value;
        m22 = value;
        
        return this;
    }
    
    
    public void m00(float m00)
    {
        this.m00 = m00;
    }
    
    public void m01(float m01)
    {
        this.m01 = m01;
    }
    
    public void m02(float m02)
    {
        this.m02 = m02;
    }
    
    public void m10(float m10)
    {
        this.m10 = m10;
    }
    
    public void m11(float m11)
    {
        this.m11 = m11;
    }
    
    public void m12(float m12)
    {
        this.m12 = m12;
    }
    
    public void m20(float m20)
    {
        this.m20 = m20;
    }
    
    public void m21(float m21)
    {
        this.m21 = m21;
    }
    
    public void m22(float m22)
    {
        this.m22 = m22;
    }
    
    
    public float m00()
    {
        return m00;
    }
    
    public float m01()
    {
        return m01;
    }
    
    public float m02()
    {
        return m02;
    }
    
    public float m10()
    {
        return m10;
    }
    
    public float m11()
    {
        return m11;
    }
    
    public float m12()
    {
        return m12;
    }
    
    public float m20()
    {
        return m20;
    }
    
    public float m21()
    {
        return m21;
    }
    
    public float m22()
    {
        return m22;
    }
    

    

    
    @Override
    public int getRows()
    {
        return 3;
    }
    
    @Override
    public int getColumns()
    {
        return 3;
    }
    
    @Override
    public String toString()
    {
        return "{[" + m00() + ", " + m01() + "]" + "\n [" +
                m10() + ", " + m11() + "]}";
    }
}
