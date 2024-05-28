package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.entity.texture.Material;
import com.anthonycosenza.engine.space.node.Renderable;
import com.anthonycosenza.engine.space.shape.ShapeBuilder;

import java.util.List;

public class Mesh3D extends Node3D implements Renderable
{
    Model model;
    public Mesh3D()
    {
        super();
        Material material = new Material();
        material.getMeshes().add(ShapeBuilder.plane(100, 100));
        model = new Model(List.of(material));
        setPosition(0f, -20f, 0f);
    }
    @Override
    public Model getModel()
    {
        return model;
    }
}
