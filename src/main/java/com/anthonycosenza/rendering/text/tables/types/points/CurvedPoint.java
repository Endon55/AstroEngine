package com.anthonycosenza.rendering.text.tables.types.points;


import com.anthonycosenza.math.vector.Vector2;
import com.anthonycosenza.math.vector.Vector2i;

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
    
}
