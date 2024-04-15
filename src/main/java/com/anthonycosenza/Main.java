package com.anthonycosenza;


import com.anthonycosenza.math.vector.Vector3;
import org.joml.Matrix4f;

public class Main
{
    public static void main(String[] args)
    {
/*        Vector3 vec = new Vector3();
        System.out.println(vec);
        System.out.println(vec.getUnit());*/
        Engine engine = new Engine();
        engine.run();
    }
}