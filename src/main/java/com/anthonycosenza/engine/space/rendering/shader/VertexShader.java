package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.util.FileUtils;
import org.lwjgl.opengl.GL20;

public class VertexShader implements Shader
{
    public static final VertexShader DEFAULT = new VertexShader("AstroEngine/resources/shaders/scene.vert");
    public static final String DEFAULT_SHADER_CODE = FileUtils.getFileContents("AstroEngine/resources/shaders/default.vert");
    private long resourceID = -1;
    private String shaderpath;
    
    //Specifically only to make the default shader.
    private VertexShader(String filepath)
    {
        resourceID = -10000;
        shaderpath = filepath;
    }
    
    public VertexShader(String filepath, long resourceID)
    {
        setResourceID(resourceID);
        shaderpath = filepath;
    }
    public VertexShader() { }
    
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
        return GL20.GL_VERTEX_SHADER;
    }
    
    @Override
    public String getShaderCode()
    {
        return FileUtils.getFileContents(shaderpath);
    }
}
