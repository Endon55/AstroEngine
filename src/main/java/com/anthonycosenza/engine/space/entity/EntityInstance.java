package com.anthonycosenza.engine.space.entity;

import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityInstance
{
    private final Model model;
    private final Matrix4f instanceMatrix;
    private final Vector3f position;
    private final Quaternionf rotation;
    private final Vector3f scale;
    private final ShaderPipeline shaderPipeline;
    
    
    public EntityInstance(Model model, float px, float py, float pz, float qx, float qy, float qz, float qw, float sx, float sy, float sz, ShaderPipeline shaderPipeline)
    {
        this.instanceMatrix = new Matrix4f();
        
        this.model = model;
        this.position = new Vector3f(px, py, pz);
        this.rotation = new Quaternionf(qx, qy, qz, qw);
        this.scale = new Vector3f(sx, sy, sz);
        this.shaderPipeline = shaderPipeline;
        updateMatrix();
    }
    public EntityInstance(Model model, Vector3f position, Quaternionf rotation, Vector3f scale, ShaderPipeline shaderPipeline)
    {
        this(model, position.x(), position.y(), position.z(), rotation.x(), rotation.y(), rotation.z(), rotation.w(), scale.x(), scale.y(), scale.z(), shaderPipeline);
    }
    
    public void setPosition(Vector3f position)
    {
        this.position.set(position);
        updateMatrix();
    }
    
    public void setPosition(float x, float y, float z)
    {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        updateMatrix();
    }
    
    public void rotate(float x, float y, float z, float angleDeg)
    {
        rotation.fromAxisAngleDeg(x, y, z, angleDeg);
        updateMatrix();
    }
    
    public Model getModel()
    {
        return model;
    }
    
    public Matrix4f getTransformation()
    {
        return instanceMatrix;
    }
    
    public void updateMatrix()
    {
        instanceMatrix.translationRotateScale(position, rotation, scale);
    }
}
