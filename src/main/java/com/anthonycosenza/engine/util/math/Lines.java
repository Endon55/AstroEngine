package com.anthonycosenza.engine.util.math;

import com.anthonycosenza.engine.util.math.vector.Vector2i;

public class Lines
{
    /*
     * https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
     *
     */
    public static Vector2i intersection(Vector2i a1, Vector2i b1, Vector2i a2, Vector2i b2)
    {
        int aY = b1.y() - a1.y();
        int aX = a1.x() - b1.x();
        int c1 = aY * a1.x() + aX * a1.y();
    
        int bY = b2.y() - a2.y();
        int bX = a2.x() - b2.x();
        int c2 = bY * a2.x() + bX * a2.y();
        
        int determinant = aY * bX - bY * aX;
        
        if(determinant == 0) return null;
        
        return new Vector2i((int) Math.round((bX * c1 - aX * c2) / (double)determinant),
        (int)Math.round((aY * c2 - bY * c1) / (double)determinant));
        
    }
    
    public static long intersects2(int x, Vector2i a2, Vector2i b2)
    {
        double m = (b2.x() - a2.x())/(double)(b2.y() - a2.y());
        m *= x + a2.y();
        
        return Math.round(m);
        
    }
    
    public static boolean checkY(int yCheck, int y0, int y1)
    {
        if(y1 < y0)
        {
            return yCheck >= y1 && yCheck <= y0;
        }
        return yCheck >= y0 && yCheck <= y1;
    }
    
    public static boolean checkX(int xCheck, int x0, int x1)
    {
        if(x1 < x0)
        {
            return xCheck >= x1 && xCheck <= x0;
        }
        return xCheck >= x0 && xCheck <= x1;
    }
    public static boolean isHorizontal(Vector2i p1,Vector2i p2)
    {
        return p1.y() == p2.y();
    }
    
    public static boolean isVertical(Vector2i p1, Vector2i p2)
    {
        return p1.x() == p2.x();
    }
    
}
