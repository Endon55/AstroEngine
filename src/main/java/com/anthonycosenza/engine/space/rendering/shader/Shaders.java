package com.anthonycosenza.engine.space.rendering.shader;

import java.util.List;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Shaders
{
    public static final ShaderPipeline DEFAULT_SHADER_PIPELINE = new ShaderPipeline(
            new ShaderData("AstroEngine/resources/shaders/scene.vert", GL_VERTEX_SHADER),
            new ShaderData("AstroEngine/resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
    
    public static ShaderPipeline BUILD_SHADER_PIPELINE(List<Shader> shaders)
    {
        //Check if this list of shaders has been added before and if it has, link that Shader pipeline instead?
        //that might be optimizing too early though.
        throw new RuntimeException("Code up build shader pipeline.");
    }
}
