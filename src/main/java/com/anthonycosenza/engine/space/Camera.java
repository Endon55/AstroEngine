package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.util.math.matrix.Matrix4;
import com.anthonycosenza.engine.util.math.vector.Vector2;
import com.anthonycosenza.engine.util.math.vector.Vector3;

public class Camera
{
    //https://www.tomdalling.com/blog/modern-opengl/04-cameras-vectors-and-input/
    
    private Vector3 position;
    //We don't need a z component really unless we plan to have the camera go upside down.
    private Vector3 rotation;
    
    private Vector3 xAxisLocal;
    private Vector3 yAxisLocal;
    private Vector3 zAxisLocal;
    
    private Matrix4 cameraMatrix;
    
    
    public Camera()
    {
        position = new Vector3();
        rotation = new Vector3();
        
        xAxisLocal = new Vector3();
        yAxisLocal = new Vector3();
        zAxisLocal = new Vector3();
        
        cameraMatrix = new Matrix4();
        updateMatrix();
    }
    
    public void rotateDeg(Vector2 rotation)
    {
        this.rotation.add(rotation.y(), rotation.x(), 0);
        updateMatrix();
    }
    
    public void rotateDeg(float x, float y)
    {
        this.rotation.add(y, x, 0);
        updateMatrix();
    }
    public void setRotationDeg(Vector2 rotation)
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
    
    public Vector3 getPosition()
    {
        return position;
    }
    
    public Vector3 getRotation()
    {
        return rotation;
    }
    
    public void moveLocalX(float distance)
    {
        xAxisLocal.mult(distance);
        position.add(xAxisLocal);
        updateMatrix();
    }
    
    public void moveLocalY(float distance)
    {
        yAxisLocal.mult(distance);
        position.add(yAxisLocal);
        updateMatrix();
    }

    public void moveLocalZ(float distance)
    {
        zAxisLocal.mult(distance);
        position.add(zAxisLocal);
        updateMatrix();
    }
    
    public void moveGlobalX(float distance)
    {
        position.addX(distance);
        updateMatrix();
    }
    
    public void moveGlobalY(float distance)
    {
        position.addY(distance);
        updateMatrix();
    }
    
    public void moveGlobalZ(float distance)
    {
        position.addZ(distance);
        updateMatrix();
    }
    
    public Vector3 direction()
    {
        return new Vector3();
    }
    
    public void updateMatrix()
    {
        cameraMatrix
                //Sets the matrix to diagonal 1s
                .identity()
                //Essentially what we're doing here is
                // <<--------------------------------------<<
                // translation * rotationZ * rotationY * rotationX
                .rotateX(rotation.x()).rotateY(rotation.y()).rotateZ(-rotation.z())
                .translate(-position.x(), -position.y(), position.z())
                //Then once ive calculated what the matrix should look like, I extract the vectors that point in the x, y, and z axis relative to the cameras current rotation,
                // not relative to the global axis.
                .positiveAxis(xAxisLocal, yAxisLocal, zAxisLocal);
    }
    
    
    
    public Matrix4 getMatrix()
    {
        return cameraMatrix;
    }
}
