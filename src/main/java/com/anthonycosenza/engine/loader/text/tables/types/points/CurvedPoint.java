package com.anthonycosenza.engine.loader.text.tables.types.points;


import com.anthonycosenza.engine.util.math.vector.Vector2i;

public class CurvedPoint implements FontPoint
{
    private int hintMask;
    private Vector2i position;
    private int width;
    private Vector2i controlPoint1;
    private Vector2i controlPoint2;
    
    public CurvedPoint(int xa, int ya, int xb, int yb, int posX, int posY)
    {
        position = new Vector2i(posX, posY);
        controlPoint1 = new Vector2i(xa, ya);
        controlPoint2 = new Vector2i(xb, yb);
    }
    
    @Override
    public Vector2i getPosition()
    {
        return position;
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
