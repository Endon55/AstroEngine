package com.anthonycosenza.engine.space.rendering.shader;

import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderPipeline
{
    private transient final int programID;
    private transient final UniformMap uniforms;
    public transient final long resourceID;

    public ShaderPipeline(long resourceID, int... compileIDs)
    {
        programID = glCreateProgram();
        
        for(int shader : compileIDs)
        {
            glAttachShader(programID, shader);
        }
        
        glLinkProgram(programID);
    
        if(glGetProgrami(programID, GL_LINK_STATUS) == 0)
        {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID));
        }
        
        
        for(int shader : compileIDs)
        {
            glDetachShader(programID, shader);
        }
        uniforms = new UniformMap(programID);
        this.resourceID = resourceID;
    }
    /*public ShaderPipeline(ShaderData... shaders)
    {
        programID = glCreateProgram();
        List<Integer> shaderIDs = new ArrayList<>();
        for(ShaderData shader : shaders)
        {
            shaderIDs.add(compile(shader));
        }
        //Once all the shaders are compiled we need to tell OpenGL that this ShaderPipeline is ready for use, and OpenGL will link them into an internal Object.
        glLinkProgram(programID);
        
        if(glGetProgrami(programID, GL_LINK_STATUS) == 0)
        {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID));
        }
        
        //We no longer need the compiled shader source anymore as OpenGL has already linked a copy of it.
        shaderIDs.forEach((shaderID) ->
        {
            glDetachShader(programID, shaderID);
            glDeleteShader(shaderID);
        });
        uniforms = new UniformMap(programID);
    }
    */
    public void bind()
    {
        glUseProgram(programID);
    }
    
    public UniformMap getUniforms()
    {
        return uniforms;
    }
    
    public void unbind()
    {
        glUseProgram(0);
    }
    
    public int getProgramID()
    {
        return programID;
    }
    
    public void cleanup()
    {
        glDeleteProgram(programID);
    }
    
    public long getResourceID()
    {
        return resourceID;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        ShaderPipeline pipeline = (ShaderPipeline) o;
    
        return programID == pipeline.programID;
    }
    
    @Override
    public int hashCode()
    {
        return programID;
    }
}
