package com.anthonycosenza.text.tables.types.points;

import com.anthonycosenza.math.vector.Vector2i;

public class StraightPoint implements FontPoint
{
    private int hintMask;
    private Vector2i position;
    private int width;
    
    public StraightPoint(int x, int y)
    {
        width = 0;
        this.hintMask = 0;
        position = new Vector2i(x, y);
    }
    
    public StraightPoint(Vector2i point)
    {
        width = 0;
        this.hintMask = 0;
        position = new Vector2i(point);
    }
    public StraightPoint setHintMask(int hintMask)
    {
        this.hintMask = hintMask;
        return this;
    }
    public StraightPoint setWidth(int width)
    {
        this.width = width;
        return this;
    }
    
    public Vector2i getPosition()
    {
        return position;
    }
}
