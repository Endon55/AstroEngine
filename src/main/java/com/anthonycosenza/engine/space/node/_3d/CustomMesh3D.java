package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.space.rendering.materials.ShaderMaterial;

public class CustomMesh3D extends Mesh3D
{
    @Override
    public void initialize()
    {
        super.initialize();
        ((ShaderMaterial)material).setUniform("wavelength", 1f);
        ((ShaderMaterial) material).setUniform("amplitude", 3f);
        ((ShaderMaterial) material).setUniform("speed", 5f);
    }
    
    float time = 0;
    @Override
    public void update(float delta)
    {
        super.update(delta);
        ShaderMaterial shaderMaterial = (ShaderMaterial) material;
        time += delta;
        shaderMaterial.setUniform("time", time);
    }
   
}
