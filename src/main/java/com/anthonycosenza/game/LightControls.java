package com.anthonycosenza.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.render.gui.IGuiInstance;
import com.anthonycosenza.engine.render.light.AmbientLight;
import com.anthonycosenza.engine.render.light.DirectionalLight;
import com.anthonycosenza.engine.render.light.PointLight;
import com.anthonycosenza.engine.render.light.SceneLighting;
import com.anthonycosenza.engine.render.light.SpotLight;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class LightControls implements IGuiInstance
{

    
    private float[] ambientColor;
    private float[] ambientFactor;
    private float[] dirConeX;
    private float[] dirConeY;
    private float[] dirConeZ;
    private float[] dirLightColor;
    private float[] dirLightIntensity;
    private float[] dirLightX;
    private float[] dirLightY;
    private float[] dirLightZ;
    private float[] pointLightColor;
    private float[] pointLightIntensity;
    private float[] pointLightX;
    private float[] pointLightY;
    private float[] pointLightZ;
    private float[] spotLightColor;
    private float[] spotLightCuttoff;
    private float[] spotLightIntensity;
    private float[] spotLightX;
    private float[] spotLightY;
    private float[] spotLightZ;
    
    public LightControls(Scene scene)
    {
        SceneLighting sceneLighting = scene.getSceneLighting();
        AmbientLight ambientLight = sceneLighting.getAmbientLight();
        Vector3f color = ambientLight.getColor();
        
        ambientFactor = new float[]{ambientLight.getIntensity()};
        ambientColor = new float[]{color.x, color.y, color.z};
        
        PointLight pointLight = sceneLighting.getPointLights().get(0);
        color = pointLight.getColor();
        Vector3f pos = pointLight.getPosition();
        pointLightColor = new float[]{color.x, color.y, color.z};
        pointLightX = new float[]{pos.x};
        pointLightY = new float[]{pos.y};
        pointLightZ = new float[]{pos.z};
        pointLightIntensity = new float[]{pointLight.getIntensity()};
        
        SpotLight spotLight = sceneLighting.getSpotLights().get(0);
        pointLight = spotLight.getPointLight();
        color = pointLight.getColor();
        pos = pointLight.getPosition();
        spotLightColor = new float[]{color.x, color.y, color.z};
        spotLightX = new float[]{pos.x};
        spotLightY = new float[]{pos.y};
        spotLightZ = new float[]{pos.z};
        spotLightIntensity = new float[]{pointLight.getIntensity()};
        spotLightCuttoff = new float[]{spotLight.getCutOffAngle()};
        Vector3f coneDir = spotLight.getConeDirection();
        dirConeX = new float[]{coneDir.x};
        dirConeY = new float[]{coneDir.y};
        dirConeZ = new float[]{coneDir.z};
        
        DirectionalLight directionalLight = sceneLighting.getDirectionalLight();
        color = directionalLight.getColor();
        pos = directionalLight.getDirection();
        dirLightColor = new float[]{color.x, color.y, color.z};
        dirLightX = new float[]{pos.x};
        dirLightY = new float[]{pos.y};
        dirLightZ = new float[]{pos.z};
        dirLightIntensity = new float[]{directionalLight.getIntensity()};
    }
    
    @Override
    public void drawGui()
    {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(450, 400);
        
        ImGui.begin("Lights controls");
        if(ImGui.collapsingHeader("Ambient Light"))
        {
            ImGui.sliderFloat("Ambient factor", ambientFactor, 0.0f, 1.0f, "%.2f");
            ImGui.colorEdit3("Ambient color", ambientColor);
        }
        
        if(ImGui.collapsingHeader("Point Light"))
        {
            ImGui.sliderFloat("Point Light - x", pointLightX, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Point Light - y", pointLightY, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Point Light - z", pointLightZ, -10.0f, 10.0f, "%.2f");
            ImGui.colorEdit3("Point Light color", pointLightColor);
            ImGui.sliderFloat("Point Light Intensity", pointLightIntensity, 0.0f, 1.0f, "%.2f");
        }
        
        if(ImGui.collapsingHeader("Spot Light"))
        {
            ImGui.sliderFloat("Spot Light - x", spotLightX, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Spot Light - y", spotLightY, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Spot Light - z", spotLightZ, -10.0f, 10.0f, "%.2f");
            ImGui.colorEdit3("Spot Light color", spotLightColor);
            ImGui.sliderFloat("Spot Light Intensity", spotLightIntensity, 0.0f, 1.0f, "%.2f");
            ImGui.separator();
            ImGui.sliderFloat("Spot Light cutoff", spotLightCuttoff, 0.0f, 360.0f, "%2.f");
            ImGui.sliderFloat("Dir cone - x", dirConeX, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Dir cone - y", dirConeY, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Dir cone - z", dirConeZ, -1.0f, 1.0f, "%.2f");
        }
        
        if(ImGui.collapsingHeader("Dir Light"))
        {
            ImGui.sliderFloat("Dir Light - x", dirLightX, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Dir Light - y", dirLightY, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Dir Light - z", dirLightZ, -1.0f, 1.0f, "%.2f");
            ImGui.colorEdit3("Dir Light color", dirLightColor);
            ImGui.sliderFloat("Dir Light Intensity", dirLightIntensity, 0.0f, 1.0f, "%.2f");
        }
        
        ImGui.end();
        ImGui.endFrame();
        ImGui.render();
    }
    
    @Override
    public boolean handleGuiInput(Scene scene, Window window)
    {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();
        imGuiIO.setMousePos(mousePos.x, mousePos.y);
        imGuiIO.setMouseDown(0, mouseInput.isLeftButtonPressed());
        imGuiIO.setMouseDown(1, mouseInput.isRightButtonPressed());
        
        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        if(consumed)
        {
            SceneLighting sceneLighting = scene.getSceneLighting();
            AmbientLight ambientLight = sceneLighting.getAmbientLight();
            ambientLight.setIntensity(ambientFactor[0]);
            ambientLight.setColor(ambientColor[0], ambientColor[1], ambientColor[2]);
            
            PointLight pointLight = sceneLighting.getPointLights().get(0);
            pointLight.setPosition(pointLightX[0], pointLightY[0], pointLightZ[0]);
            pointLight.setColor(pointLightColor[0], pointLightColor[1], pointLightColor[2]);
            pointLight.setIntensity(pointLightIntensity[0]);
            
            SpotLight spotLight = sceneLighting.getSpotLights().get(0);
            pointLight = spotLight.getPointLight();
            pointLight.setPosition(spotLightX[0], spotLightY[0], spotLightZ[0]);
            pointLight.setColor(spotLightColor[0], spotLightColor[1], spotLightColor[2]);
            pointLight.setIntensity(spotLightIntensity[0]);
            spotLight.setCutOffAngle(spotLightColor[0]);
            spotLight.setConeDirection(dirConeX[0], dirConeY[0], dirConeZ[0]);
            
            DirectionalLight directionalLight = sceneLighting.getDirectionalLight();
            directionalLight.setDirection(dirLightX[0], dirLightY[0], dirLightZ[0]);
            directionalLight.setColor(dirLightColor[0], dirLightColor[1], dirLightColor[2]);
            directionalLight.setIntensity(dirLightIntensity[0]);
        }
        return consumed;
    }
}
