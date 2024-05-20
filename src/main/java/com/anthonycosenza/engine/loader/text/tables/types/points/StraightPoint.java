package com.anthonycosenza.engine.loader.text.tables.types.points;

import com.anthonycosenza.engine.util.math.vector.Vector2;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

public class StraightPoint implements FontPoint
{
    private int hintMask;
    private final Vector2 position;
    private int width;
    
    public StraightPoint(Vector2 point)
    {
        this(0, point.x(), point.y());
    }
    
    public StraightPoint(int hintMask, float x, float y)
    {
        width = 0;
        this.hintMask = hintMask;
        position = new Vector2(x, y);
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
    
    public Vector2 getPosition()
    {
        return position;
    }
    
    @Override
    public FontPoint scale(float scale)
    {
        this.position.mult(scale);
        return this;
    }
    
    @Override
    public int getHintMask()
    {
        return hintMask;
    }
    
    @Override
    public FontPoint copy()
    {
        return new StraightPoint(hintMask, position.x(), position.y());
    }
    
    @Override
    public String toString()
    {
        return "Straight{" +
                "position=" + position +
                '}';
    }
}
