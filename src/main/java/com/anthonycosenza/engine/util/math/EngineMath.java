package com.anthonycosenza.engine.util.math;

public class EngineMath
{
    private static final float RADS_IN_DEGREE = .01745329f;
    

    public static float toRadians(float degrees, boolean clamp)
    {
        return (clamp ? degrees % 360 : degrees) * RADS_IN_DEGREE;
    }
    public static float toRadians(float degrees)
    {
        return toRadians(degrees, false);
    }
    
    public static int highestPowerOf2(int number)
    {
        return (int) Math.pow(2, (int) log2(number));
    }
    
    public static double log2(double number)
    {
        return Math.log(number) / Math.log(2);
    }
    
    
    public static int clamp(int value, int lowerBound, int upperBound)
    {
        return Math.max(Math.min(value, upperBound), lowerBound);
    }
    
    public static float clamp(float value, float lowerBound, float upperBound)
    {
        return Math.max(Math.min(value, upperBound), lowerBound);
    }
    
}
