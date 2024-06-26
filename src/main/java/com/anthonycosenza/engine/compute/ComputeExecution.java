package com.anthonycosenza.engine.compute;

public abstract class ComputeExecution implements Execution
{
    private boolean initialized = false;
    
    public void execute()
    {
        if(!initialized)
        {
            initialize();
            initialized = true;
        }
        compute();
    }
    
    
    protected abstract void initialize();
    protected abstract void compute();
}
