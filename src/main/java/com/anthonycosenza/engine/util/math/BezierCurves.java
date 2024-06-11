package com.anthonycosenza.engine.util.math;

import org.joml.Vector2f;

public class BezierCurves
{

    /*
     * This Bézier curve algorithm calculates the x, y pixel coordinate at time t,
     * to get an accurate graphing the number of timesteps needs to be the number of pixels.
     * Obviously you can't know that easily so call the other function instead where it's
     * estimated based on distance.
     */
    public static Vector2f bezier(float time, Vector2f... points)
    {
        //System.out.println(Arrays.toString(points));
        int pointCount = points.length;
        if(pointCount < 3 || pointCount > 7)
            throw new RuntimeException("Bezier doesn't support less than 3 or more than 7 points : " + pointCount);
        
        /*
         * Determine which function to call with the number of arguments given.
         */
        float x;
        float y;
        switch(pointCount)
        {
            case 3 ->
            {
                x = bezierQuadratic(points[0].x(), points[1].x(), points[2].x(), time);
                y = bezierQuadratic(points[0].y(), points[1].y(), points[2].y(), time);
            }
            case 4 ->
            {
                x = bezierCubic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), time);
                y = bezierCubic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), time);
            }
            case 5 ->
            {
                x = bezierQuartic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), points[4].x(), time);
                y = bezierQuartic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), points[4].y(), time);
            }
            case 6 ->
            {
                x = bezierQuintic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), points[4].x(), points[5].x(), time);
                y = bezierQuintic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), points[4].y(), points[5].y(), time);
            }
            case 7 ->
            {
                x = bezierSextic(points[0].x(), points[1].x(), points[2].x(), points[3].x(), points[4].x(), points[5].x(), points[6].x(), time);
                y = bezierSextic(points[0].y(), points[1].y(), points[2].y(), points[3].y(), points[4].y(), points[5].y(), points[6].y(), time);
            }
            default -> throw new RuntimeException("We shouldn't be here.");
        }
    return new Vector2f( x, y);

    }
    
    private static float mixBezier(float a, float b, float t)
    {
        // degree 1
        return a * (1.0f - t) + b * t;
    }
    
    private static float bezierQuadratic(float A, float B, float C, float t)
    {
        // degree 2
        float AB = mixBezier(A, B, t);
        float BC = mixBezier(B, C, t);
        return mixBezier(AB, BC, t);
    }
    
    private static float bezierCubic(float A, float B, float C, float D, float t)
    {
        // degree 3
        float ABC = bezierQuadratic(A, B, C, t);
        float BCD = bezierQuadratic(B, C, D, t);
        return mixBezier(ABC, BCD, t);
    }
    
    private static float bezierQuartic(float A, float B, float C, float D, float E, float t)
    {
        // degree 4
        float ABCD = bezierCubic(A, B, C, D, t);
        float BCDE = bezierCubic(B, C, D, E, t);
        return mixBezier(ABCD, BCDE, t);
    }
    
    private static float bezierQuintic(float A, float B, float C, float D, float E, float F, float t)
    {
        // degree 5
        float ABCDE = bezierQuartic(A, B, C, D, E, t);
        float BCDEF = bezierQuartic(B, C, D, E, F, t);
        return mixBezier(ABCDE, BCDEF, t);
    }
    
    private static float bezierSextic(float A, float B, float C, float D, float E, float F, float G, float t)
    {
        // degree 6
        float ABCDEF = bezierQuintic(A, B, C, D, E, F, t);
        float BCDEFG = bezierQuintic(B, C, D, E, F, G, t);
        return mixBezier(ABCDEF, BCDEFG, t);
    }
}
