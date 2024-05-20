package com.anthonycosenza.engine.loader.text.tables.types.points;


import com.anthonycosenza.engine.util.math.vector.Vector2;

public class CurvedPoint implements FontPoint
{
    private int hintMask;
    private final Vector2 position;
    private int width;
    private final Vector2 controlPoint1;
    private final Vector2 controlPoint2;
    
    
    public CurvedPoint(int hintMask, float xa, float ya, float xb, float yb, float posX, float posY)
    {
        this.hintMask = hintMask;
        position = new Vector2(posX, posY);
        controlPoint1 = new Vector2(xa, ya);
        controlPoint2 = new Vector2(xb, yb);
    }
    
    public CurvedPoint(float xa, float ya, float xb, float yb, float posX, float posY)
    {
        hintMask = 0;
        position = new Vector2(posX, posY);
        controlPoint1 = new Vector2(xa, ya);
        controlPoint2 = new Vector2(xb, yb);
    }
    
    public CurvedPoint setHintMask(int hintMask)
    {
        this.hintMask = hintMask;
        return this;
    }
    
    @Override
    public Vector2 getPosition()
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
        controlPoint1.mult(scale);
        controlPoint2.mult(scale);
        position.mult(scale);
        return this;
    }
    
    @Override
    public FontPoint copy()
    {
        return new CurvedPoint(hintMask, controlPoint1.x(), controlPoint1.y(), controlPoint2.x(), controlPoint2.y(), position.x(), position.y());
    }
    
    public Vector2 getControlPoint1()
    {
        return controlPoint1;
    }
    
    public Vector2 getControlPoint2()
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
