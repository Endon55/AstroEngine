package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.node.Renderable;
import com.anthonycosenza.engine.space.rendering.materials.Material;

import java.util.List;

public class Model3D extends Node3D implements Renderable
{
    
    public Model model;
    
    
    @Override
    public List<Material> getMaterials()
    {
        return model.getMaterials();
    }
}
