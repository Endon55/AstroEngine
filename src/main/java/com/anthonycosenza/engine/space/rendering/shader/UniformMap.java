package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.engine.util.math.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformMap
{
    int shaderPipelineID;
    private Map<String, Integer> uniformsMap;
    
    public UniformMap(int shaderPipelineID)
    {
        this.shaderPipelineID = shaderPipelineID;
        uniformsMap = new HashMap<>();
        createUniform("entityMatrix", "projectionMatrix", "cameraMatrix", "textureSampler", "material.diffuse");
    }
    
    public void createUniform(String uniformName)
    {
        uniformsMap.put(uniformName, glGetUniformLocation(shaderPipelineID, uniformName));
    }
    
    public void createUniform(String... uniformNames)
    {
        for(String uniformName : uniformNames)
        {
            uniformsMap.put(uniformName, glGetUniformLocation(shaderPipelineID, uniformName));
        }
    }
    
    public int getUniformID(String uniformName)
    {
        Integer id = uniformsMap.get(uniformName);
        if(id == null)
        {
            createUniform(uniformName);
            id = uniformsMap.get(uniformName);
            if(id == null) throw new RuntimeException("Couldn't make uniform when getting: " + uniformName);
        }
        return id;
    }
    
    public void setUniform(String uniformName, int data)
    {
        glUniform1i(getUniformID(uniformName), data);
    }
    
    public void setUniform(String uniformName, float data)
    {
        glUniform1f(getUniformID(uniformName), data);
    }
    
    public void setUniform(String uniformName, float data1, float data2)
    {
        glUniform2f(getUniformID(uniformName), data1, data2);
    }
    
    public void setUniform(String uniformName, Color data)
    {
        glUniform4f(getUniformID(uniformName), data.r(), data.g(), data.b(), data.a());
    }
    
    public void setUniform(String uniformName, Vector2f data)
    {
        glUniform2f(getUniformID(uniformName), data.x(), data.y());
    }
    
    public void setUniform(String uniformName, Matrix4f data)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(getUniformID(uniformName), false, data.get(stack.mallocFloat(16)));
        }
    }

    
}
