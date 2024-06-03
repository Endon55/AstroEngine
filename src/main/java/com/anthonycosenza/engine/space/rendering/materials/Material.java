package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;

public interface Material
{
    void bind();
    void set();
    ShaderPipeline getShaderPipeline();
}
