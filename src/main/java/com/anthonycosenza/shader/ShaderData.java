package com.anthonycosenza.shader;

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
