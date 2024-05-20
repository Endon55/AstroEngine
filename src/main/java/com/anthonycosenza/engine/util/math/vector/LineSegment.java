package com.anthonycosenza.engine.util.math.vector;

public class LineSegment
{
    private Vector2 point1;
    private Vector2 point2;
    
    
    
    public LineSegment(Vector2 point1, Vector2 point2)
    {
        this.point1 = point1;
        this.point2 = point2;
    }
    
    
    public Vector2 getPoint1()
    {
        return point1;
    }
    
    public Vector2 getPoint2()
    {
        return point2;
    }
    
    public float getSlope()
    {
        return (point2.y() - point1.y()) / (point2.x() - point1.x());
    }
    
    public Vector2 intersection(Vector2 point1, Vector2 point2)
    {
        float aY = this.point2.y() - this.point1.y();
        float aX = this.point1.x() - this.point2.x();
        float c1 = aY * this.point1.x() + aX * this.point1.y();
        
        float bY = point2.y() - point1.y();
        float bX = point1.x() - point2.x();
        float c2 = bY * point1.x() + bX * point1.y();
        
        float determinant = aY * bX - bY * aX;
        
        if(determinant == 0) return null;
        
        return new Vector2((bX * c1 - aX * c2) / determinant,
                (aY * c2 - bY * c1) / determinant);
        
    }
    
    public boolean withinEdgeY(float yCheck)
    {
        if(point1.y() > point2.y())
        {
            return yCheck >= point2.y() && yCheck < point1.y();
        }
        return yCheck >= point1.y() && yCheck < point2.y();
    }
    
    public boolean withinEdgeX(float xCheck)
    {
        if(point1.x() > point2.x())
        {
            return xCheck >= point2.x() && xCheck < point1.x();
        }
        return xCheck >= point1.x() && xCheck < point2.x();
    }
    
    
    @Override
    public String toString()
    {
        return  "{{" + point1.x() + ", " + point1.y() + "}" +
                "{" + point2.x() + ", " + point2.y() + "}}";
    }
}
