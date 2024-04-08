package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.Utils;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * This class compiles shaders and hands them off to OpenGL
 */
public class ShaderProgram
{
    private final int programID;
    
    public ShaderProgram(List<ShaderModuleData> shaderModuleDataList, String geometryShaderFile)
    {
        programID = glCreateProgram();
        if(programID == 0)
        {
            throw new RuntimeException("Could not create Shader");
        }
        if(geometryShaderFile != null&& geometryShaderFile.length() > 0)
        {
        }
    
        List<Integer> shaderModules = new ArrayList<>();
        shaderModuleDataList.forEach(shader -> shaderModules.add(createShader(Utils.loadResource(shader.shaderFile), shader.shaderType)));
    
        link(shaderModules);
    }
    public ShaderProgram(List<ShaderModuleData> shaderModuleDataList)
    {
        this(shaderModuleDataList, "");
    }

    protected int createShader(String shaderCode, int shaderType)
    {
        int shaderID = glCreateShader(shaderType);
        if(shaderID == 0) throw new RuntimeException("Error creating shader. Type: " + shaderType);
        
        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);
        
        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
        {
            throw new RuntimeException("Error compiling shader code: " + glGetShaderInfoLog(shaderID, 1024));
        }
        glAttachShader(programID, shaderID);
        return shaderID;
    }
    
    public void link(List<Integer> shaderModules)
    {
        glLinkProgram(programID);
        if(glGetProgrami(programID, GL_LINK_STATUS) == 0)
        {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
        }
        
        shaderModules.forEach(s->glDetachShader(programID, s));
        shaderModules.forEach(GL30::glDeleteShader);
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
    
    public void cleanup()
    {
        unbind();
        if(programID != 0)
        {
            glDeleteProgram(programID);
        }
    }
    public record ShaderModuleData(String shaderFile, int shaderType)
    {
    
    }
}
