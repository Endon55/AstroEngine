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
    private Vector2f displayVector;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private Vector2f previousPos;
    private boolean rightButtonPressed;
    
    public MouseInput(long windowHandle)
    {
        previousPos = new Vector2f(-1, -1);
        currentPos = new Vector2f();
        displayVector = new Vector2f();
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindow = false;
        //Updates local mouse position whenever the cursor is moved, updates regardless of being inside the window or not.
        glfwSetCursorPosCallback(windowHandle, (handle, xPos, yPos) ->
        {
            currentPos.x = (float) xPos;
            currentPos.y = (float) yPos;
        });
        //Updates when the cursor leaves or enters the window
        glfwSetCursorEnterCallback(windowHandle, (handle, entered) ->
        {
            inWindow = entered;
        });
        //Updates when a mouse button is pressed, updates regardless of being inside the window or not.
        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mode) ->
        {
            leftButtonPressed  = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }
    
    public Vector2f getCurrentPos()
    {
        return currentPos;
    }
    
    public Vector2f getDisplayVector()
    {
        return displayVector;
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
        displayVector.x = 0;
        displayVector.y = 0;
        if(previousPos.x > 0 && previousPos.y > 0 && inWindow)
        {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if(rotateX)
            {
                displayVector.y = (float) deltaX;
            }
            if(rotateY)
            {
                displayVector.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }
}
