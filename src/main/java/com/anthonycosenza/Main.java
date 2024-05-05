package com.anthonycosenza;


import com.anthonycosenza.loader.HuffmanModel;
import com.anthonycosenza.loader.ImageLoader;
import com.anthonycosenza.test.Benchmark;

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
    
        //HuffmanModel encoding = new HuffmanModel("Anthony is the Best in the WOrld11");
        
        //int[] image = ImageLoader.load("resources/images/Ai Sasha.png");
        //ImageLoader loader = new ImageLoader("resources/images/Penguin.png");
    

        
        Engine engine = new Engine();
        engine.run();
    }
}