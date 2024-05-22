package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.util.math.matrix.Matrix4;
import com.anthonycosenza.engine.util.math.vector.Vector2;
import com.anthonycosenza.engine.util.math.vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera
{
    //https://www.tomdalling.com/blog/modern-opengl/04-cameras-vectors-and-input/
    
    private Vector3f position;
    //We don't need a z component really unless we plan to have the camera go upside down.
    private Vector3f rotation;
    
    private Vector3f xAxisLocal;
    private Vector3f yAxisLocal;
    private Vector3f zAxisLocal;
    
    private Matrix4f cameraMatrix;
    
    
    public Camera()
    {
        position = new Vector3f();
        rotation = new Vector3f();
        
        xAxisLocal = new Vector3f();
        yAxisLocal = new Vector3f();
        zAxisLocal = new Vector3f();
        
        cameraMatrix = new Matrix4f();
        updateMatrix();
    }
    
    public void addRotation(float x, float y)
    {
        rotation.add(x, y, 0);
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
    /*
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
    */

    
    
    
    public Matrix4f getMatrix()
    {
        return cameraMatrix;
    }
}
