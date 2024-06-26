package com.anthonycosenza.engine.space.node;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.compute.Execution;

import java.util.ArrayList;
import java.util.List;

public class Scene extends Node implements Asset
{
    private final List<Execution> executions;
    public Scene()
    {
        executions = new ArrayList<>();
    }
    @Override
    protected long createNodeID()
    {
        return AssetManager.getInstance().generateResourceID();
    }
    
    public List<Execution> getExecutions()
    {
        return executions;
    }
    
    public void addExecution(Execution execution)
    {
        executions.add(execution);
    }
}
