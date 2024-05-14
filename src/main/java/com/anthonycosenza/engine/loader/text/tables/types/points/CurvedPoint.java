package com.anthonycosenza.engine.loader.text.tables.types.points;


import com.anthonycosenza.engine.util.math.vector.Vector2i;

public class CurvedPoint implements FontPoint
{
    private int hintMask;
    private final Vector2i position;
    private int width;
    private final Vector2i controlPoint1;
    private final Vector2i controlPoint2;
    
    
    public CurvedPoint(int hintMask, int xa, int ya, int xb, int yb, int posX, int posY)
    {
        this.hintMask = hintMask;
        position = new Vector2i(posX, posY);
        controlPoint1 = new Vector2i(xa, ya);
        controlPoint2 = new Vector2i(xb, yb);
    }
    
    public CurvedPoint(int xa, int ya, int xb, int yb, int posX, int posY)
    {
        position = new Vector2i(posX, posY);
        controlPoint1 = new Vector2i(xa, ya);
        controlPoint2 = new Vector2i(xb, yb);
    }
    
    public CurvedPoint setHintMask(int hintMask)
    {
        this.hintMask = hintMask;
        return this;
    }
    
    @Override
    public Vector2i getPosition()
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
    
    public Vector2i getControlPoint1()
    {
        return controlPoint1;
    }
    
    public Vector2i getControlPoint2()
    {
        return controlPoint2;
    }
    
    @Override
    public String toString()
    {
        return "CurvedPoint{" +
                "hintMask=" + hintMask +
                ", position=" + position +
                ", width=" + width +
                ", controlPoint1=" + controlPoint1 +
                ", controlPoint2=" + controlPoint2 +
                '}';
    }
}
