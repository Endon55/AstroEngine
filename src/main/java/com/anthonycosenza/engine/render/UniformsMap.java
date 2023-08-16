package com.anthonycosenza.engine.render;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
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
    
    public void createUniform(String uniformName)
    {
        int uniformLocation = glGetUniformLocation(programID, uniformName);
        if(uniformLocation < 0)
        {
            throw new RuntimeException("Could not find uniform [" + uniformName + "] in shader program[" + programID + "]");
        }
        uniforms.put(uniformName, uniformLocation);
    }
    
    public void setUniform(String uniformName, Matrix4f value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            Integer location = uniforms.get(uniformName);
            if(location == null)
            {
                throw new RuntimeException("Could not find uniform [" + uniformName + "]");
            }
            glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
        }
    }
    
    
    
}
