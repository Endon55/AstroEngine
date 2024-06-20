package com.anthonycosenza.engine.space.node;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.input.Input;
import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;
import com.anthonycosenza.engine.util.math.EngineMath;
import org.joml.Vector2f;

public class MoveableCamera extends Camera
{
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
    private boolean moved = true;
    
    public void reset()
    {
        setPosition(0, 0, 0);
        setRotationDeg(0, 0, 0);
        moved();
    }
    private void moved()
    {
        this.moved = true;
    }
    public void finishMoving()
    {
        this.moved = false;
    }
    public boolean isMoved()
    {
        return moved;
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
            moved();
        }
        if(input.isPressed(Key.D))
        {
            moveLocalX(deltaSpeed);
            moved();
        }
        if(input.isPressed(Key.W))
        {
            moveLocalZ(-deltaSpeed);
            moved();
        }
        if(input.isPressed(Key.S))
        {
            moveLocalZ(deltaSpeed);
            moved();
        }
        //Down
        if(input.isPressed(Key.C))
        {
            moveGlobalY(deltaSpeed);
            moved();
        }
        //Up
        if(input.isPressed(Key.SPACE))
        {
            moveGlobalY(-deltaSpeed);
            moved();
        }
    
        if(input.isMiddleMouseButtonPressed())
        {
            Vector2f pos = input.getMousePosition();
            if(lastPos != null)
            {
                float x = (pos.x - lastPos.x) * mouseSensitivity;
                float y = (pos.y - lastPos.y) * -mouseSensitivity;
                rotateDeg(x, y);
                moved();
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
