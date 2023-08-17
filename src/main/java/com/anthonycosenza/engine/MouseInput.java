package com.anthonycosenza.engine;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

public class MouseInput
{
    private Vector2f currentPos;
    private Vector2f displVec;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private Vector2f previousPos;
    private boolean rightButtonPressed;
    
    public MouseInput(long windowHandle)
    {
        previousPos = new Vector2f(-1, -1);
        currentPos = new Vector2f();
        displVec = new Vector2f();
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindow = false;
    
        glfwSetCursorPosCallback(windowHandle, (handle, xPos, yPos) ->
        {
            currentPos.x = (float) xPos;
            currentPos.y = (float) yPos;
        });
        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> inWindow = entered);
        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mode) ->
        {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button ==GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }
    
    public Vector2f getCurrentPos()
    {
        return currentPos;
    }
    
    public Vector2f getDisplVec()
    {
        return displVec;
    }
    
    public boolean isLeftButtonPressed()
    {
        return leftButtonPressed;
    }
    
    public boolean isRightButtonPressed()
    {
        return rightButtonPressed;
    }
    
    public void input()
    {
        displVec.x = 0;
        displVec.y = 0;
        if(previousPos.x > 0 && previousPos.y > 0 && inWindow)
        {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if(rotateX)
            {
                displVec.y = (float) deltaX;
            }
            if(rotateY)
            {
                displVec.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }
}
