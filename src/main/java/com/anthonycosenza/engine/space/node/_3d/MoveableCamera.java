package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;
import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.util.math.EngineMath;

public class MoveableCamera extends Camera
{
    float rotation = 0;
    float moveSpeed = 10f;
    final float minMoveSpeed = 10f;
    final float maxMoveSpeed = 1000f;
    final float moveSpeedIncrement = 2f;
    
    float rotationSpeed = 100;
    float mouseSensitivity = 20;
    final float minMouseSensitivity = 0;
    final float maxMouseSensitivity = 1000;
    final float mouseIncrement = 10;
    
    
    @Override
    public void update(float delta)
    {
        super.update(delta);
        
        
        boolean sprint = Engine.INPUT.getState(Key.LEFT_SHIFT) == KeyAction.PRESSED || Engine.INPUT.getState(Key.RIGHT_SHIFT) == KeyAction.PRESSED;
        float deltaSpeed = (moveSpeed * delta);
        if(sprint) deltaSpeed *= 2f;
    
        if(Engine.INPUT.isPressed(Key.A))
        {
            moveLocalX(-deltaSpeed);
        }
        if(Engine.INPUT.isPressed(Key.D))
        {
            moveLocalX(deltaSpeed);
        }
        if(Engine.INPUT.isPressed(Key.W))
        {
            moveLocalZ(-deltaSpeed);
        }
        if(Engine.INPUT.isPressed(Key.S))
        {
           moveLocalZ(deltaSpeed);
        }
        //Down
        if(Engine.INPUT.isPressed(Key.C))
        {
            moveGlobalY(-deltaSpeed);
        }
        //Up
        if(Engine.INPUT.isPressed(Key.SPACE))
        {
            moveGlobalY(deltaSpeed);
        }
    
    
        if(Engine.INPUT.getScrollPosition() != 0)
        {
            moveSpeed += moveSpeedIncrement * Engine.INPUT.getScrollPosition();
            moveSpeed = EngineMath.clamp(moveSpeed, minMoveSpeed, maxMoveSpeed);
        }
    
        if(!Engine.INPUT.isCursorStale() && Engine.INPUT.isMouseLocked())
        {
            float mouseDelta = mouseSensitivity * delta;
            rotateDeg(Engine.INPUT.getMouseDirection().x() * mouseDelta, Engine.INPUT.getMouseDirection().y() * mouseDelta);
        }
    }
}
