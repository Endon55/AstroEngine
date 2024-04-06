package com.anthonycosenza.engine.render.light;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SceneLighting
{
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;
    
    public SceneLighting()
    {
        ambientLight = new AmbientLight();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
    }
    
    public AmbientLight getAmbientLight()
    {
        return ambientLight;
    }
    
    public DirectionalLight getDirectionalLight()
    {
        return directionalLight;
    }
    
    public List<PointLight> getPointLights()
    {
        return pointLights;
    }
    
    public List<SpotLight> getSpotLights()
    {
        return spotLights;
    }
    
    public void setSpotLights(List<SpotLight> spotLights)
    {
        this.spotLights = spotLights;
    }
}
