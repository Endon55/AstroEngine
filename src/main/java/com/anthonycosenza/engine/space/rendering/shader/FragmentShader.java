package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.editor.GLSLBuilder;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.util.FileUtils;
import org.lwjgl.opengl.GL20;

import java.io.File;


public class FragmentShader implements Shader
{
    public static final FragmentShader DEFAULT = new FragmentShader("AstroEngine/resources/shaders/scene.frag", -10002);
    public static final String DEFAULT_SHADER_CODE = FileUtils.getFileContents("AstroEngine/resources/shaders/default.frag");
    private long resourceID = -1;
    private String shaderpath;
    public boolean assemble = false;
  
    public FragmentShader(String filepath, long resourceID)
    {
        setResourceID(resourceID);
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
        if(assemble)
        {
            return new GLSLBuilder(new File(shaderpath)).build();
        }
       else return FileUtils.getFileContents(shaderpath);
    }
    
}
