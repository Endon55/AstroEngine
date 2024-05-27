package com.anthonycosenza.engine.space.runtime;

import com.anthonycosenza.engine.space.entity.Model;
import org.joml.Matrix4f;

public interface Entity
{
    Model getModel();
    Matrix4f getMatrix();
}
