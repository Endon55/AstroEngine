package com.anthonycosenza.engine.space.rendering.projection;

import com.anthonycosenza.engine.util.math.matrix.Matrix4;

public class Projection
{
    private float fov;
    private float aspectRatio;
    private float zMin;
    private float zMax;
    private final Matrix4 projectionMatrix;
    
    /**
     *
     * @param fovDegrees how wide the camera lens is and how much of the world it captures. Bigger captures more objects but makes everything smaller.
     */
    public Projection(float fovDegrees, int width, int height, float zMin, float zMax)
    {
        this.fov = (float) Math.toRadians(fovDegrees);
        this.aspectRatio = (float) height / width;
        this.zMin = zMin;
        this.zMax = zMax;
        projectionMatrix = new Matrix4();
        
        updateMatrix();
    }
    
    public void resize(int width, int height)
    {
        aspectRatio = (float) height / (float) width;
        updateMatrix();
    }
    public void zDistance(float zNear, float zFar)
    {
        this.zMin = zNear;
        this.zMax = zFar;
        updateMatrix();
    }
    public float getFov()
    {
        return fov;
    }
    
    public void setFov(float fovDegrees)
    {
        this.fov = (float) Math.toRadians(fovDegrees);
    }
    
    public Matrix4 getMatrix()
    {
        return projectionMatrix;
    }
    
    private void updateMatrix()
    {
    /*
    For a full breakdown visit:
    https://www.songho.ca/opengl/gl_projectionmatrix.html
    
    Viewing Frustum
    
                 zMin                    zMax
                          .              ' |
              .    |           .   x       |
    camera <       x   '                   |
              '    |                       |
                          '              . |
              Screen Position         View Distance
              
    x shows approximately where it should show up on the screen after accounting for the projection.
    
    -------------------------------------------
    Perspective Matrix
    
    | (e1): zMin / (tan(fov / 2),                                    0,                                    0,                                        0 |
    |                          0, (e2): zMin / (aspect * tan(fov / 2)),                                    0,                                        0 |
    |                          0,                                    0, (e3): -(zMax + zMin) / (zMax - zMin), (e4): (-2 * zMax * zMin) / (zMax - zMin) |
    |                          0,                                    0,                            (e5):  -1,                                        0 |
    
    
    The purpose of this matrix is to take world coordinates and turn them into camera coordinates and then turn them into clip space coordinates.
    Projection Matrix = [Clip Matrix]*[Camera Matrix]*[Initial Vector]
    
    First we take the initial vector(with 4th component for w set as 1) and rotate it around the world origin by the cameras rotation, this gives an orbiting effect,
    Second we translate the coordinate by the cameras position.
    Third we draw a line between the translated coordinate and the camera and see where it intersects the zMin plane as normalized device coordinates(ndc).
    That essentially calculates where the coordinate should be placed on the screen, any coordinate outside of -1 to 1 in any axis gets clipped(hence calling it clip space).
    To get to ndc we need to use a 4th parameter w, it's kinda like a scale variable, when all the math happens the w value will get adjusted and to get a 1 back in the w position
    we divide all 4 values by w
    (x, y, z, w) -> normalized (x/w, y/w, z/w, w/w=1)
    OpenGL does this later so we pass it the un-normalized (x, y, z, w) coordinate.
    
    The e1 and e2 values come from a relationship of triangles where the ratio of x and y is always the same if the angle and x or y are the same
    so the triangle that's formed from the width of zMin to the camera center point will have the same ratio as a triangle using zMax.
    
    So we can abuse this ratio to easily find the value of x and y.
    Since we're assuming horizontal fov, the width of the triangle is going to be zMin * tan(fov / 2) fov is cut in half to form a right triangle over the center axis.
    The height is proportional to the width divided by the aspect ratio.
    
    So e1 and e2 are scale the same way, it's just that one of them also gets scaled by aspect ratio, it doesn't matter which so long as you're consistent.
    
    e3 and e4 confuse the shit out of me, so you're on your own. The link above derives these.
    
    e5 is set to -1 because the w value of clip space is -z, and since that column gets multiplied by z we set it to -1 so it becomes -1 * z = -z
     */

        float tangent = (float)Math.tan(fov / 2);
        float right = zMin * tangent;
        float top = right * aspectRatio;
        float zDist = zMax - zMin;
        
        projectionMatrix
                .m00(zMin / right).m11(zMin / top)
                .m22(-(zMax + zMin) / zDist).m23(-2 * (zMax * zMin) / zDist)
                .m32(-1).m33(0);
    }
    
}
