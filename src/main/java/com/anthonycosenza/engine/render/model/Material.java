package com.anthonycosenza.engine.render.model;

import com.anthonycosenza.engine.render.TextureCache;
import com.anthonycosenza.engine.render.model.Mesh;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material
{
    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    
    private Vector4f ambientColor;
    private Vector4f diffuseColor;
    private Vector4f specularColor;
    private float reflectance;
    
    private String texturePath;
    private String normalMapPath;
    
    private List<Mesh> meshList;
    
    public Material()
    {
        diffuseColor = DEFAULT_COLOR;
        ambientColor = DEFAULT_COLOR;
        specularColor = DEFAULT_COLOR;
        meshList = new ArrayList<>();
    }
    
    public Vector4f getAmbientColor()
    {
        return ambientColor;
    }
    
    public void setAmbientColor(Vector4f ambientColor)
    {
        this.ambientColor = ambientColor;
    }
    
    public Vector4f getSpecularColor()
    {
        return specularColor;
    }
    
    public void setSpecularColor(Vector4f specularColor)
    {
        this.specularColor = specularColor;
    }
    
    public float getReflectance()
    {
        return reflectance;
    }
    
    public void setReflectance(float reflectance)
    {
        this.reflectance = reflectance;
    }
    
    public List<Mesh> getMeshList()
    {
        return meshList;
    }
    
    public String getTexturePath()
    {
        return texturePath;
    }
    
    public void setTexturePath(String texturePath)
    {
        this.texturePath = texturePath;
    }
    
    public Vector4f getDiffuseColor()
    {
        return diffuseColor;
    }
    
    public void setDiffuseColor(Vector4f diffuseColor)
    {
        this.diffuseColor = diffuseColor;
    }
    
    public String getNormalMapPath()
    {
        return normalMapPath;
    }
    
    public void setNormalMapPath(String normalMapPath)
    {
        this.normalMapPath = normalMapPath;
    }
    
    public void setTextureDefault()
    {
        this.texturePath = TextureCache.DEFAULT_TEXTURE;
    }

    public void cleanup()
    {
        meshList.forEach(Mesh::cleanup);
    }
 
}
