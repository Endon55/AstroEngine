package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.node.Renderable;

import java.util.List;

public class Mesh3D extends Node3D implements Renderable
{
    public Mesh mesh;
    public Material material;
    
    private transient Model model;
    
    public Mesh3D()
    {
        super();
        setPosition(0f, 0f, 0f);
        
    }
    @Override
    public List<Material> getMaterials()
    {
        if(mesh != null && material != null)
        {
            material.getMeshes().add(mesh);
            return List.of(material);
        }
        return List.of();
    }
}
