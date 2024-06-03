package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;

public interface Material extends Asset
{
    void bind();
    void set();
    ShaderPipeline getShaderPipeline();
}
