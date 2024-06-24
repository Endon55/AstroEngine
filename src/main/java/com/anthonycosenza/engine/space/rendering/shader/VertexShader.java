package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.editor.GLSLBuilder;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.loader.Resources;
import com.anthonycosenza.engine.util.FileUtils;
import org.lwjgl.opengl.GL20;

import java.io.File;

public class VertexShader implements Shader
{
    public static final VertexShader DEFAULT = new VertexShader(Resources.get("shaders/scene.vert"), -10000);
    public static final String DEFAULT_SHADER_CODE = FileUtils.getFileContents(Resources.get("shaders/default.vert"));
    private long resourceID = -1;
    private String shaderPath;
    public transient boolean assemble = false;

    public VertexShader(String filepath, long resourceID)
    {
        setResourceID(resourceID);
        shaderPath = filepath;
    }
    public VertexShader() { }
    
    public void setShaderPath(String shaderPath)
    {
        this.shaderPath = shaderPath;
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
        return GL20.GL_VERTEX_SHADER;
    }
    
    @Override
    public String getShaderCode()
    {
        if(assemble)
        {
            return new GLSLBuilder(new File(shaderPath)).build();
        }
        else return FileUtils.getFileContents(shaderPath);
    }
    
    @Override
    public boolean isDefaultShader()
    {
        return this.equals(DEFAULT);
    }
}
