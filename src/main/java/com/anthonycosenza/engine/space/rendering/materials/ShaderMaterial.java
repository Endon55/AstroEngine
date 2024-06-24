package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.ShaderManager;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;

import java.util.HashSet;
import java.util.Set;

public class ShaderMaterial implements Material
{
    public long resourceID = -1;
    private final transient Set<Mesh> meshes;
    private transient ShaderPipeline pipeline = null;
    private transient long shaderHash;
    
    public VertexShader vertexShader;
    public FragmentShader fragmentShader;
    
    
    public ShaderMaterial()
    {
        this.meshes = new HashSet<>();
    }
    
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.getInstance().generateResourceID();
        }
        return resourceID;
    }
    
    public void setVertexShader(VertexShader vertexShader)
    {
        this.vertexShader = vertexShader;
    }
    
    public void setFragmentShader(FragmentShader fragmentShader)
    {
        this.fragmentShader = fragmentShader;
    }
    
    @Override
    public void setResourceID(long resourceID)
    {
        this.resourceID = resourceID;
    }
    
    public FragmentShader getFragmentShader()
    {
        return fragmentShader;
    }
    
    public VertexShader getVertexShader()
    {
        return vertexShader;
    }
    
    @Override
    public Set<Mesh> getMeshes()
    {
        return meshes;
    }
    
    @Override
    public void addMesh(Mesh mesh)
    {
        meshes.add(mesh);
    }
    
    @Override
    public void bind()
    {
    
    }
    
    public void setUniform(String uniform, int data)
    {
        getShaderPipeline().bind();
        getShaderPipeline().getUniforms().setUniform(uniform, data);
    }
    public void setUniform(String uniform, float data)
    {
        getShaderPipeline().bind();
        getShaderPipeline().getUniforms().setUniform(uniform, data);
    }
    
    public void setUniform(String uniform, float data1, float data2)
    {
        getShaderPipeline().bind();
        getShaderPipeline().getUniforms().setUniform(uniform, data1, data2);
    }
    
    @Override
    public void setUniforms(ShaderPipeline pipeline)
    {
    
    }
    public void updatePipeline()
    {
        if(vertexShader != null) vertexShader.assemble = true;
        if(fragmentShader != null) fragmentShader.assemble = true;
        shaderHash = ShaderManager.hashShaders(vertexShader, fragmentShader);
        
        pipeline = ShaderManager.createPipeline(vertexShader, fragmentShader);
    }
    @Override
    public ShaderPipeline getShaderPipeline()
    {
        if(pipeline == null || shaderHash != ShaderManager.hashShaders(vertexShader, fragmentShader))
        {
            updatePipeline();
        }
        return pipeline;
    }
}
