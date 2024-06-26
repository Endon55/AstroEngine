package com.anthonycosenza.engine.space.node;

import com.anthonycosenza.engine.assets.ShaderManager;
import com.anthonycosenza.engine.space.rendering.shader.ComputeShader;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;

public class ComputeNode extends Node
{
    protected ComputeShader computeShader;
    private transient ShaderPipeline pipeline;
    
    public void compute()
    {
    
    }
    
    public ShaderPipeline getPipeline()
    {
        if(pipeline == null)
        {
            if(computeShader == null) return null;
            else pipeline = ShaderManager.createPipeline(computeShader);
        }
        return pipeline;
    }
}
