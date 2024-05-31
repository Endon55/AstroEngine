package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.engine.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderPipeline
{
    private final int programID;
    private final UniformMap uniforms;
    
    public ShaderPipeline(ShaderData... shaders)
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
    
    public void bind()
    {
        glUseProgram(programID);
    }
    
    public void unbind()
    {
        glUseProgram(0);
    }
    
    public int getProgramID()
    {
        return programID;
    }
    
    private int compile(ShaderData shaderData)
    {
        //Register a new shader with OpenGL
        int shaderID = glCreateShader(shaderData.type);
        //Load Shader code.
        glShaderSource(shaderID, readCodeFile(shaderData.filePath));
        //Compile the loaded shader code.
        glCompileShader(shaderID);
        
        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
        {
            throw new RuntimeException("Error while compiling shader[" + shaderData.filePath + "] - " + glGetShaderInfoLog(shaderID, 1024));
        }
        //Bind the newly compiled shader to the shader context.
        glAttachShader(programID, shaderID);
        
        return shaderID;
    }
    
    private String readCodeFile(String shaderPath)
    {
        return FileUtils.getFileContents(shaderPath);
    }
    
    public void cleanup()
    {
        glDeleteProgram(programID);
    }
}
