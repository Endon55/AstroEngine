package com.anthonycosenza.test;

import java.util.Arrays;

public class Benchmark
{
    public static long NANOS_IN_SECOND = 1000000000;
    private int trialCount;
    private long[] trials1;
    private long[] trials2;
    private IBenchmark function1;
    private IBenchmark function2;
    
    public Benchmark(int trials, IBenchmark function)
    {
        this(trials, function, null);
    }
    
    public Benchmark(int trials, IBenchmark function1, IBenchmark function2)
    {
        this.trialCount = trials;
        this.function1 = function1;
        this.function2 = function2;
        this.trials1 = new long[this.trialCount];
        if(function2 != null)
        {
            this.trials2 = new long[this.trialCount];
        }
    }
    
    public void test()
    {
        long result1 = test(trials1, function1);
        if(function2 != null)
        {
            long result2 = test(trials2, function2);
    
            System.out.println("Func1 Times: " + Arrays.toString(trials1));
            System.out.println("Func1 Avg Nano Time: " + result1 + "(" + (result1 / (float) NANOS_IN_SECOND) + "s)");
            System.out.println();
            System.out.println("Func2 Times: " + Arrays.toString(trials2));
            System.out.println("Func2 Avg Nano Time: " + result1 + "(" + (result2 / (float) NANOS_IN_SECOND) + "s)");
            System.out.println();
            
            if(result1 > result2)
            {
                System.out.println("Func1 is " + (result1 / (float)result2) + " times faster than Func2");
            } else
            {
                System.out.println("Func2 is " + (result2 / (float)result1) + " times faster than Func1");
            }
            
        }
        else
        {
            System.out.println("Times: " + Arrays.toString(trials1));
            System.out.println("Avg Nano Time: " + result1 + "(" + (result1 / (float) NANOS_IN_SECOND) + "s)");
        }
        
    }
    private long test(long[] trials, IBenchmark function)
    {
        long start;
        long cumulative1 = 0;
    
        for(int i = 0; i < trialCount; i++)
        {
            start = System.nanoTime();
    
            function.call();
        
            long time = System.nanoTime() - start;
            trials[i] = time;
            cumulative1 += time;
        }
        return cumulative1 / trialCount;
    }
}
