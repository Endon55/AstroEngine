package com.anthonycosenza.game;

import com.anthonycosenza.engine.game.IGameLogic;
import com.anthonycosenza.engine.render.Model;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.render.Mesh;
import com.anthonycosenza.engine.window.Window;
import org.joml.Quaternionf;

import java.util.List;

public class TestGame implements IGameLogic
{
    Scene scene;
    Entity cube1;
    
    @Override
    public void init(Window window)
    {
        scene = new Scene(window.getWidth(), window.getHeight());
        Mesh triangleMesh = new Mesh(new float[]{
                // VO
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        }, new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        },new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,});
        scene.addModel("CubeModel", new Model("CubeMesh", List.of(triangleMesh)));
        cube1 = new Entity("Cube1", "CubeModel");
        cube1.setPosition(0.0f, 0.0f, -10f);
        cube1.updateModelMatrix();
        scene.addEntity(cube1);
    
    }
    
    @Override
    public void input(Window window)
    {
    
    }
    
    @Override
    public void update(float interval)
    {
        Quaternionf quat = cube1.getRotation();
        
        //cube1.setPosition(cube1.getPosition().x - .01f, 0, 0);
        cube1.setRotation(.5f, 0.5f, 0.0f, quat.angle() + .1f);
        System.out.println(quat.angle());
        //cube1.setRotationRad(1.0f, 0.0f, 0.0f, 25);
        cube1.updateModelMatrix();
    }
    
    @Override
    public void render(Window window)
    {
    
    }
    
    @Override
    public void cleanup()
    {
    
    }
    
    @Override
    public Scene getScene()
    {
        return scene;
    }
}
