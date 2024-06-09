package com.anthonycosenza.engine.space.rendering.shader;

import org.lwjgl.opengl.GL20;

public enum ShaderType
{
    VERTEX(GL20.GL_VERTEX_SHADER),
    FRAGMENT(GL20.GL_FRAGMENT_SHADER),
    ;
    
    int glShaderID;
    ShaderType(int glShaderID)
    {
        this.glShaderID = glShaderID;
    }
    
    public int getGlShaderID()
    {
        return glShaderID;
    }
}
