package com.anthonycosenza.engine.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformsMap
{
    private int programID;
    private Map<String, Integer> uniforms;
    
    public UniformsMap(int programID)
    {
        this.programID = programID;
        uniforms = new HashMap<>();
    }
    
    private int getUniformLocation(String uniformName)
    {
        Integer location = uniforms.get(uniformName);
        if(location == null)
        {
            throw new RuntimeException("Could not find uniform [" + uniformName + "]");
        }
        return location;
    }
    
    public void createUniform(String uniformName)
    {
        int uniformLocation = glGetUniformLocation(programID, uniformName);
        if(uniformLocation < 0)
        {
            throw new RuntimeException("Could not find uniform [" + uniformName + "] in shader program[" + programID + "]");
        }
        uniforms.put(uniformName, uniformLocation);
    }
    public void setUniform(String uniformName, float value)
    {
        glUniform1f(getUniformLocation(uniformName), value);
    }
    public void setUniform(String uniformName, int value)
    {
        glUniform1i(getUniformLocation(uniformName), value);
    }
    
    public void setUniform(String uniformName, Vector3f value)
    {
        glUniform3f(getUniformLocation(uniformName), value.x, value.y, value.z);
    }
    public void setUniform(String uniformName, Vector4f value)
    {
        glUniform4f(getUniformLocation(uniformName), value.x, value.y, value.z, value.w);
    }
    
    public void setUniform(String uniformName, Vector2f value)
    {
        glUniform2f(getUniformLocation(uniformName), value.x, value.y);
    }
    public void setUniform(String uniformName, Matrix4f value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(getUniformLocation(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }
    
    public void setUniform(String uniformName, Matrix4f[] matrices)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer buffer = stack.mallocFloat(16 * length);
            for(int i = 0; i < length; i++)
            {
                matrices[i].get(16 * i, buffer);
            }
            glUniformMatrix4fv(getUniformLocation(uniformName), false, buffer);
        }
    }
    
}
