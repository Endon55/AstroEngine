package com.anthonycosenza;


import com.anthonycosenza.text.FontData;

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