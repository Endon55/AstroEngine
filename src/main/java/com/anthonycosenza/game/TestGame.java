package com.anthonycosenza.game;

import com.anthonycosenza.engine.game.IGameLogic;
import com.anthonycosenza.engine.game.Scene;
import com.anthonycosenza.engine.render.Mesh;
import com.anthonycosenza.engine.window.Window;

public class TestGame implements IGameLogic
{
    Scene scene;
    
    @Override
    public void init() throws Exception
    {
        scene = new Scene();
        Mesh triangleMesh = new Mesh(new float[]{
                -0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
        }, new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        },new int[]{
                0, 1, 3, 3, 1, 2});
        scene.addMesh("cube", triangleMesh);
    }
    
    @Override
    public void input(Window window)
    {
    
    }
    
    @Override
    public void update(float interval)
    {
    
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
