package com.anthonycosenza.engine.space.rendering.shader;

import com.anthonycosenza.engine.assets.Asset;

public interface Shader extends Asset
{
    int getShaderType();
    String getShaderCode();
}
