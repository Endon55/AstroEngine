package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.util.FileUtils;
import org.lwjgl.opengl.GL20;


public class FragmentShader implements Shader
{
    public static final FragmentShader DEFAULT = new FragmentShader("AstroEngine/resources/shaders/scene.frag");
    private long resourceID = -1;
    private String shaderpath;
    
    //Specifically only to make the default shader.
    private FragmentShader(String filepath)
    {
        resourceID = -10002;
        shaderpath = filepath;
    }
    public FragmentShader()
    {
    
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
        return GL20.GL_FRAGMENT_SHADER;
    }
    
    @Override
    public String getShaderCode()
    {
        return FileUtils.getFileContents(shaderpath);
    }
    
}
