package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.node.Renderable;
public class Model3D extends Node3D implements Renderable
{
    
    @Property
    public Model model;
    
    @Override
    public Model getModel()
    {
        return model;
    }
}
