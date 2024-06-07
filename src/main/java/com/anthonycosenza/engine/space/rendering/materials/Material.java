package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;

import java.util.Set;

public interface Material extends Asset
{
    Set<Mesh> getMeshes();
    void addMesh(Mesh mesh);
    void bind();
    void set(ShaderPipeline pipeline);
    ShaderPipeline getShaderPipeline();
}
