package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.node.Renderable;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;

import java.util.List;

public class Mesh3D extends Node3D implements Renderable
{
    private Mesh mesh;
    private Material material;
    
    private transient Model model;
    
    public Mesh3D()
    {
        super();
        setPosition(0f, 0f, 0f);
    }
    @Override
    public List<Material> getMaterials()
    {
        if(material == null)
        {
            material = new StandardMaterial();
        }
        if(mesh != null)
        {
            material.getMeshes().add(mesh);
            return List.of(material);
        }
        return List.of();
    }
    
    public void setMesh(Mesh mesh)
    {
        this.mesh = mesh;
        material.addMesh(this.mesh);
    }
    
    public Mesh getMesh()
    {
        return mesh;
    }
    
    public void setMaterial(Material material)
    {
        if(mesh != null) material.addMesh(this.mesh);
        this.material = material;
    }
    
    public Material getMaterial()
    {
        return material;
    }
}
