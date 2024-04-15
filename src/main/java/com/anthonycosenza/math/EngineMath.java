package com.anthonycosenza.math;

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
}
