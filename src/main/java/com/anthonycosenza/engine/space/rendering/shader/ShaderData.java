package com.anthonycosenza.engine.space.rendering.shader;

public class ShaderData
{
    public String filePath;
    public int type;
    
    public ShaderData(String shaderFilePath, int shaderType)
    {
        this.filePath = shaderFilePath;
        this.type = shaderType;
    }
}
