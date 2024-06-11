package com.anthonycosenza.engine.space.node._3d;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;
import com.anthonycosenza.engine.space.Camera;
import com.anthonycosenza.engine.util.math.EngineMath;
import org.joml.Vector2f;

public class MoveableCamera extends Camera
{
    float rotation = 0;
    float moveSpeed = 10f;
    final float minMoveSpeed = 10f;
    final float maxMoveSpeed = 1000f;
    final float moveSpeedIncrement = 2f;
    
    float rotationSpeed = 100;
    float mouseSensitivity = .01f;
    final float minMouseSensitivity = 0;
    final float maxMouseSensitivity = 1000;
    final float mouseIncrement = 10;
    Vector2f lastPos = null;
    
    public void reset()
    {
        setPosition(0, 0, 0);
        setRotationDeg(0, 0, 0);
    }
    
    @Override
    public void update(float delta)
    {
        super.update(delta);
        Input input = Engine.INPUT;
        
        
        boolean sprint = input.getState(Key.LEFT_SHIFT) == KeyAction.PRESSED || input.getState(Key.RIGHT_SHIFT) == KeyAction.PRESSED;
        float deltaSpeed = (moveSpeed * delta);
        if(sprint) deltaSpeed *= 2f;
    
        if(input.isPressed(Key.A))
        {
            moveLocalX(-deltaSpeed);
        }
        if(input.isPressed(Key.D))
        {
            moveLocalX(deltaSpeed);
        }
        if(input.isPressed(Key.W))
        {
            moveLocalZ(-deltaSpeed);
        }
        if(input.isPressed(Key.S))
        {
           moveLocalZ(deltaSpeed);
        }
        //Down
        if(input.isPressed(Key.C))
        {
            moveGlobalY(deltaSpeed);
        }
        //Up
        if(input.isPressed(Key.SPACE))
        {
            moveGlobalY(-deltaSpeed);
        }
    
        if(input.isMiddleMouseButtonPressed())
        {
            Vector2f pos = input.getMousePosition();
            System.out.println(pos);
            if(lastPos != null)
            {
                float x = (pos.x - lastPos.x) * mouseSensitivity;
                float y = (pos.y - lastPos.y) * -mouseSensitivity;
                rotateDeg(x, y);
            }
            
            lastPos = pos;
        }
        else lastPos = null;
        
        if(input.getScrollPosition() != 0)
        {
            moveSpeed += moveSpeedIncrement * input.getScrollPosition();
            moveSpeed = EngineMath.clamp(moveSpeed, minMoveSpeed, maxMoveSpeed);
        }
    
        if(!input.isCursorStale() && input.isMouseLocked())
        {
            float mouseDelta = mouseSensitivity * delta;
            rotateDeg(input.getMouseDirection().x() * mouseDelta, input.getMouseDirection().y() * mouseDelta);
        }
    }
}
