package com.anthonycosenza;

import com.anthonycosenza.math.matrix.Matrix4;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformMap
{
    int shaderPipelineID;
    private Map<String, Integer> uniformsMap;
    
    public UniformMap(int shaderPipelineID)
    {
        this.shaderPipelineID = shaderPipelineID;
        uniformsMap = new HashMap<>();
    }
    
    public void createUniform(String uniformName)
    {
        uniformsMap.put(uniformName, glGetUniformLocation(shaderPipelineID, uniformName));
    }
    
    public int getUniformID(String uniformName)
    {
        Integer id = uniformsMap.get(uniformName);
        if(id == null)
        {
            throw new RuntimeException("Could not find Uniform[" +  uniformName + "]" + " in uniform map.");
        }
        
        return id;
    }
    
    public void setUniform(String uniformName, int data)
    {
        glUniform1i(getUniformID(uniformName), data);
    }
    public void setUniform(String uniformName, Matrix4f data)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(getUniformID(uniformName), false, data.get(stack.mallocFloat(16)));
        }
    }
    
    public void setUniform(String uniformName, Matrix4 data)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            glUniformMatrix4fv(getUniformID(uniformName), false, data.get(stack.mallocFloat(16)));
        }
    }
    
}
