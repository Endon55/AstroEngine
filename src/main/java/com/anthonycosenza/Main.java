package com.anthonycosenza;


import com.anthonycosenza.math.EngineMath;
import com.anthonycosenza.math.matrix.Matrix4;
import com.anthonycosenza.math.vector.Vector3;
import com.anthonycosenza.math.vector.Vector4;
import com.anthonycosenza.projection.Projection;
import org.joml.Matrix4f;

import java.text.NumberFormat;

public class Main
{
    public static void main(String[] args)
    {
/*        Matrix4 mat1 = new Projection(30, 1920, 1080, 1, 1000).getMatrix();
        System.out.println(mat1);
        Vector4 vec = new Vector4(0, 0, 20, 1);
        System.out.println("Vec: " + vec);
        System.out.println("Mult: " + mat1.mult(vec));
        System.out.println("/W: " + vec.mult(1 / vec.w()));*/


        
        Engine engine = new Engine();
        engine.run();
    }
}