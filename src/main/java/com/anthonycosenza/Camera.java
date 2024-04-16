package com.anthonycosenza;

import com.anthonycosenza.math.matrix.Matrix4;
import com.anthonycosenza.math.vector.Vector2;
import com.anthonycosenza.math.vector.Vector3;

public class Camera
{
    //https://www.tomdalling.com/blog/modern-opengl/04-cameras-vectors-and-input/
    
    private Vector3 direction;
    private Vector3 position;
    //We don't need a z component really unless we plan to have the camera go upside down.
    private Vector3 rotation;
    
    private Vector3 xAxisLocal;
    private Vector3 yAxisLocal;
    private Vector3 zAxisLocal;
    
    private Matrix4 cameraMatrix;
    
    
    public Camera()
    {
        direction = new Vector3();
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
        this.rotation.add(rotation.x(), rotation.y(), 0);
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
    
    public void updateMatrix()
    {
        cameraMatrix
                //Sets the matrix to diagonal 1s
                .identity()
                //Applies the rotation we're storing separately.
                //We swap the y and x values to be more intuitive otherwise x controls up/down and y controls left/right
                .rotateX(rotation.y).rotateY(rotation.x).rotateZ(rotation.z)
                //Stores a copy of the newly rotated matrix in rotationMatrix
                .extractAxis(xAxisLocal, yAxisLocal, zAxisLocal)
                //Applies the position we're storing separately.
                .translate(-position.x, -position.y, position.z);
    }
    
    public Matrix4 getMatrix()
    {
        return cameraMatrix;
    }
}
