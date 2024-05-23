package com.anthonycosenza.engine.space.entity;

import com.anthonycosenza.engine.space.rendering.shader.Shader;
import com.anthonycosenza.engine.space.rendering.shader.Shaders;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class Entity
{
    private Model model;
    private Vector3f position;
    //We use a quaternion to store rotation as it prevents "Gimbal Lock" essentially if 2 axis have the same value they get locked into place and the math can't break them out.
    //Quaternions are like 4d imaginary numbers, for some reason that's well beyond my paygrade it makes it much easier to track rotation with quats.
    private Quaternionf rotation;
    private Vector3f scale;
    private ShaderPipeline shaderPipeline;
    
    public Entity(Model model)
    {
        this.model = model;
        position = new Vector3f(0, 0, 0);
        rotation = new Quaternionf();
        scale = new Vector3f(1, 1, 1);
        shaderPipeline = Shaders.DEFAULT_SHADER_PIPELINE;
    }
    
    public void setShader(List<Shader> shaders)
    {
        shaderPipeline = Shaders.BUILD_SHADER_PIPELINE(shaders);
    }

    public Model getModel()
    {
        return model;
    }
    
    public EntityInstance spawnInstance()
    {
        return new EntityInstance(model, position, rotation, scale, shaderPipeline);
    }
    
    public EntityInstance spawnInstance(float xPos, float yPos, float zPos)
    {
        return new EntityInstance(model, position.x() + xPos, position.y() + yPos, position.z() + zPos, rotation.x(), rotation.y(), rotation.z(), rotation.w(), scale.x(), scale.y(), scale.z(), shaderPipeline);
    }
    
    public EntityInstance spawnInstance(float xPos, float yPos, float zPos, float xRotation, float yRotation, float zRotation)
    {
        return new EntityInstance(model, position.x() + xPos, position.y() + yPos, position.z() + zPos, rotation.x() + xRotation, rotation.y() + yRotation, rotation.z() + zRotation, rotation.w(), scale.x(), scale.y(), scale.z(), shaderPipeline);
    }

}
