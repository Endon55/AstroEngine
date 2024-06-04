package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.node.Renderable;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;

import java.util.List;

public class Mesh3D extends Node3D implements Renderable
{
    @Property
    Mesh mesh;
    @Property
    Material material;
    
    private Model model;
    
    public Mesh3D()
    {
        super();
        setPosition(0f, 0f, 0f);
    }
    @Override
    public Model getModel()
    {
        if(model == null && mesh != null)
        {
            if(material == null)
            {
                material = new StandardMaterial();
                material.getMeshes().add(mesh);
                model = new Model(List.of(material));
            }
        }
        return model;
    }
}
