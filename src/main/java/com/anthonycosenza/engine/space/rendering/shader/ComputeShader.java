package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.util.FileUtils;
import org.lwjgl.opengl.GL44;


public class ComputeShader implements Shader
{
    private long resourceID = -1;
    private String shaderPath;
    
    public ComputeShader(String filepath)
    {
        shaderPath = filepath;
    }
    
    public ComputeShader()
    {
    
    }
    public String getShaderPath()
    {
        return shaderPath;
    }
    
    
    @Override
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.getInstance().generateResourceID();
        }
        return resourceID;
    }
    
    @Override
    public void setResourceID(long resourceID)
    {
        this.resourceID = resourceID;
    }
    
    @Override
    public int getShaderType()
    {
        return GL44.GL_COMPUTE_SHADER;
    }
    
    @Override
    public String getShaderCode()
    {
        return FileUtils.getFileContents(shaderPath);
    }
    
    @Override
    public boolean isDefaultShader()
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return "ComputeShader{" +
                "shaderPath='" + shaderPath + '\'' +
                '}';
    }
}
