package com.anthonycosenza.engine.util.math;

import com.anthonycosenza.engine.util.math.vector.Vector2;
import com.anthonycosenza.engine.util.math.vector.Vector2i;

public class Lines
{
    /*
     * https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
     *
     */
    public static Vector2 intersection(Vector2i a1, Vector2i b1, Vector2i a2, Vector2i b2)
    {
        int aY = b1.y() - a1.y();
        int aX = a1.x() - b1.x();
        int c1 = aY * a1.x() + aX * a1.y();
    
        int bY = b2.y() - a2.y();
        int bX = a2.x() - b2.x();
        int c2 = bY * a2.x() + bX * a2.y();
        
        int determinant = aY * bX - bY * aX;
        
        if(determinant == 0) return null;
        
        return new Vector2((bX * c1 - aX * c2) / (float)determinant,
        (aY * c2 - bY * c1) / (float)determinant);
        
    }
    
    public static Vector2 intersection(Vector2 a1, Vector2 b1, Vector2 a2, Vector2 b2)
    {
        float aY = b1.y() - a1.y();
        float aX = a1.x() - b1.x();
        float c1 = aY * a1.x() + aX * a1.y();
    
        float bY = b2.y() - a2.y();
        float bX = a2.x() - b2.x();
        float c2 = bY * a2.x() + bX * a2.y();
    
        float determinant = aY * bX - bY * aX;
        
        if(determinant == 0) return null;
        
        return new Vector2((bX * c1 - aX * c2) / determinant,
                (aY * c2 - bY * c1) / determinant);
        
    }
    
    public static long intersects2(int x, Vector2i a2, Vector2i b2)
    {
        double m = (b2.x() - a2.x())/(double)(b2.y() - a2.y());
        m *= x + a2.y();
        
        return Math.round(m);
        
    }
    
    
    public static double angle(Vector2 intersection, Vector2 point1, Vector2 point2)
    {
        //Center the vectors to the origin(the intersection)
        float p1x = point1.x() - intersection.x();
        float p1y = point1.y() - intersection.y();
        float p2x = point2.x() - intersection.x();
        float p2y = point2.y() - intersection.y();
        
        double angle1 = Math.atan2(p1y, p1x);
        double angle2 = Math.atan2(p2y, p2x);
        
        return (angle2 - angle1);
    }
    
    public static double angle(Vector2i intersection, Vector2i point1, Vector2i point2)
    {
        //Center the vectors to the origin(the intersection)
        float p1x = point1.x() - intersection.x();
        float p1y = point1.y() - intersection.y();
        float p2x = point2.x() - intersection.x();
        float p2y = point2.y() - intersection.y();
        
        double angle1 = Math.atan2(p1y, p1x);
        double angle2 = Math.atan2(p2y, p2x);
        double pi2 = Math.PI * 2;
        double shift = pi2 - angle2;
        angle1 += shift;
        angle1 %= pi2;
        /*if(angle1 < 0)
        {
            angle1 += pi2;
        }*/
        //return Math.min(angle1, pi2 - angle1);
        return  angle1;
        //return (angle2 - angle1);
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
    
    public static boolean checkY(float yCheck, float y0, float y1)
    {
        if(y1 < y0)
        {
            return yCheck >= y1 && yCheck <= y0;
        }
        return yCheck >= y0 && yCheck <= y1;
    }
    
    public static boolean checkX(float xCheck, float x0, float x1)
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
    
    public static boolean isHorizontal(Vector2 p1, Vector2 p2)
    {
        return p1.y() == p2.y();
    }
    
    public static boolean isVertical(Vector2 p1, Vector2 p2)
    {
        return p1.x() == p2.x();
    }
    
}
