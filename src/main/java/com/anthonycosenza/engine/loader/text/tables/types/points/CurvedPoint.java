package com.anthonycosenza.engine.loader.text.tables.types.points;


import org.joml.Vector2f;

public class CurvedPoint implements FontPoint
{
    private int hintMask;
    private final Vector2f position;
    private int width;
    private final Vector2f controlPoint1;
    private final Vector2f controlPoint2;
    
    
    public CurvedPoint(int hintMask, float xa, float ya, float xb, float yb, float posX, float posY)
    {
        this.hintMask = hintMask;
        position = new Vector2f(posX, posY);
        controlPoint1 = new Vector2f(xa, ya);
        controlPoint2 = new Vector2f(xb, yb);
    }
    
    public CurvedPoint(float xa, float ya, float xb, float yb, float posX, float posY)
    {
        hintMask = 0;
        position = new Vector2f(posX, posY);
        controlPoint1 = new Vector2f(xa, ya);
        controlPoint2 = new Vector2f(xb, yb);
    }
    
    public CurvedPoint setHintMask(int hintMask)
    {
        this.hintMask = hintMask;
        return this;
    }
    
    @Override
    public Vector2f getPosition()
    {
        return position;
    }
    
    @Override
    public int getHintMask()
    {
        return hintMask;
    }
    
    @Override
    public FontPoint scale(float scale)
    {
        controlPoint1.mul(scale);
        controlPoint2.mul(scale);
        position.mul(scale);
        return this;
    }
    
    @Override
    public FontPoint copy()
    {
        return new CurvedPoint(hintMask, controlPoint1.x(), controlPoint1.y(), controlPoint2.x(), controlPoint2.y(), position.x(), position.y());
    }
    
    public Vector2f getControlPoint1()
    {
        return controlPoint1;
    }
    
    public Vector2f getControlPoint2()
    {
        return controlPoint2;
    }
    
    @Override
    public String toString()
    {
        return "Curved{" +
                "pos=" + position +
                ", cp1=" + controlPoint1 +
                ", cp2=" + controlPoint2 +
                '}';
    }
}
