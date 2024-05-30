package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.space.node.Node;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera extends Node
{
    public static Camera DEFAULT_CAMERA = new Camera();
    
    //https://www.tomdalling.com/blog/modern-opengl/04-cameras-vectors-and-input/
    
    @Property
    public Vector3f position;
    //We don't need a z component really unless we plan to have the camera go upside down.
    @Property
    public Vector3f rotation;
    
    
    private Vector3f xAxisLocal;
    private Vector3f yAxisLocal;
    private Vector3f zAxisLocal;
    
    private Matrix4f cameraMatrix;
    
    
    public Camera()
    {
        name = "Camera";
        position = new Vector3f();
        rotation = new Vector3f();
        
        xAxisLocal = new Vector3f();
        yAxisLocal = new Vector3f();
        zAxisLocal = new Vector3f();
        
        cameraMatrix = new Matrix4f();
        updateMatrix();
    }
    
    
    @Override
    public void update(float delta)
    {
        super.update(delta);
        
        
    }
    
    public void addRotation(float x, float y)
    {
        rotation.add(x, y, 0);
    }
    
    public void rotateDeg(Vector2f rotation)
    {
        this.rotation.add(rotation.y(), rotation.x(), 0);
        updateMatrix();
    }
    
    public void rotateDeg(float x, float y)
    {
        this.rotation.add(y, x, 0);
        updateMatrix();
    }
    public void setRotationDeg(Vector2f rotation)
    {
        this.rotation.set(rotation.x(), rotation.y(), 0);
        updateMatrix();
    }
    
    public void setRotationDeg(float x, float y, float z)
    {
        this.rotation.set(x, y, z);
        updateMatrix();
    }
    
    public void setPosition(float x, float y, float z)
    {
        this.position.set(x, y, z);
        updateMatrix();
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
    
    public Vector3f getRotation()
    {
        return rotation;
    }
    
    
    public void moveLocalX(float distance)
    {
        cameraMatrix.positiveX(xAxisLocal).mul(distance);
        position.add(xAxisLocal);
        updateMatrix();
    }
    
    public void moveLocalY(float distance)
    {
        cameraMatrix.positiveY(yAxisLocal).mul(distance);
        position.add(yAxisLocal);
        updateMatrix();
    }
    
    public void moveLocalZ(float distance)
    {
        cameraMatrix.positiveZ(zAxisLocal).mul(distance);
        position.add(zAxisLocal);
        updateMatrix();
    }
    
    public void moveGlobalX(float distance)
    {
        position.add(distance, 0, 0);
        updateMatrix();
    }
    
    public void moveGlobalY(float distance)
    {
        position.add(0, distance, 0);
        updateMatrix();
    }
    
    public void moveGlobalZ(float distance)
    {
        position.add(0, 0, distance);
        updateMatrix();
    }
    
    public void updateMatrix()
    {
        cameraMatrix
                .identity()
                .rotateX(rotation.x())
                .rotateY(rotation.y())
                .rotateZ(-rotation.z())
                .translate(-position.x(), -position.y(), -position.z());
                
    }

    
    
    public Matrix4f getMatrix()
    {
        return cameraMatrix;
    }
}
