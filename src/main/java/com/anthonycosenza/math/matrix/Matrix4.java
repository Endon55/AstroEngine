package com.anthonycosenza.math.matrix;

import com.anthonycosenza.math.vector.Vector2;
import com.anthonycosenza.math.vector.Vector3;
import com.anthonycosenza.math.vector.Vector4;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

public class Matrix4 implements IMatrix
{
    /*
        For all 4x4 Matrix Math
        https://www.euclideanspace.com/maths/geometry/affine/matrix4x4/index.htm
        
           Matrix Structure
        | m00, m01, m02, m03 |
        | m10, m11, m12, m13 |
        | m20, m21, m22, m23 |
        | m30, m31, m32, m33 |
        
     */
    
    private float m00, m01, m02, m03;
    private float m10, m11, m12, m13;
    private float m20, m21, m22, m23;
    private float m30, m31, m32, m33;

    public Matrix4()
    {
        identity();
    }
    
    public Matrix4(float fillValue)
    {
        fill(fillValue);
    }
    
    public Matrix4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }
    
    public Matrix4 translate(Vector3 vector)
    {
        return translate(vector.x(), vector.y(), vector.z());
    }
    
    public Matrix4 translate(float x, float y, float z)
    {
        return this.mult(new Matrix4().m03(x).m13(y).m23(z));
        //return this.m03(x).m13(y).m23(z);
    }
    
    
    public Matrix3 getMatrix3()
    {
        return new Matrix3(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }
    
    public Matrix4 set(Matrix3 matrix)
    {
        m00(matrix.m00());
        m01(matrix.m01());
        m02(matrix.m02());
    
        m10(matrix.m10());
        m11(matrix.m11());
        m12(matrix.m12());
    
        m20(matrix.m20());
        m21(matrix.m21());
        m22(matrix.m22());
        
        return this;
    }
    
    public Matrix4 rotate(Vector2 angle)
    {
        return rotate(angle.x(), angle.y());
    }
    
    public Matrix4 rotate(float xAngle, float yAngle)
    {
        return this.rotateX(xAngle).rotateY(yAngle);
    }
    
    public Matrix4 rotate(Vector3 angle)
    {
        return rotate(angle.x(), angle.y(), angle.z());
    }
    public Matrix4 rotate(float xAngle, float yAngle, float zAngle)
    {
        return this.rotateX(xAngle).rotateY(yAngle).rotateZ(zAngle);
    }
    
    public Matrix4 rotateX(float angle)
    {
        /*
          x | 1,       0,      0 |
          y | 0, cos(θ), -sin(θ) |
          z | 0, sin(θ),  cos(θ) |
          
            If you think of each line in a 3x3 matrix as the x y and z component of a vector,
            then it's intuitive that rotating around the x-axis doesn't have any impact on the x coordinates
            and fully shifts all the points on z and y.
          x | 1,       0,      0, 0 |
          y | 0, cos(θ), -sin(θ), 0 |
          z | 0, sin(θ), cos(θ), 0 |
          w | 0,       0,      0, 1 |
          
          The rotation component sits in the top left of our transformation matrix.
         */
    
        double rads = Math.toRadians(angle);
        Matrix4 xMatrix = new Matrix4();
        float cos = (float)Math.cos(rads);
        float sin = (float)Math.sin(rads);
        
        xMatrix.m11(cos).m12(-sin);
        xMatrix.m21(sin).m22(cos);
        
        return this.mult(xMatrix);
    }
    
    public Matrix4 rotateY(float angle)
    {
        /*
          x |  cos(θ),  0,  sin(θ) |
          y |      0,   1,       0 |
          z | -sin(θ),  0,  cos(θ) |
          
            If you think of each line in a 3x3 matrix as the x y and z component of a vector,
            then it's intuitive that rotating around the y-axis doesn't have any impact on the y coordinates
            and fully shifts all the points on x and z.
          x |  cos(θ),  0, sin(θ), 0 |
          y |      0,   1,       0, 0 |
          z | -sin(θ),  0,  cos(θ), 0 |
          w |      0,   0,       0, 1 |
          
          The rotation component sits in the top left of our transformation matrix.
         */
        
        double rads = Math.toRadians(angle);
        Matrix4 yMatrix = new Matrix4();
        float cos = (float) Math.cos(rads);
        float sin = (float) Math.sin(rads);
    
        yMatrix.m00(cos).m02(sin);
        yMatrix.m20(-sin).m22(cos);
        
        return this.mult(yMatrix);
    }
    
    public Matrix4 rotateZ(float angle)
    {
        /*
          x |  cos(θ), sin(θ),   0 |
          y | -sin(θ), cos(θ),   0 |
          z |       0,      0,   1 |
          
            If you think of each line in a 3x3 matrix as the x y and z component of a vector,
            then it's intuitive that rotating around the z-axis doesn't have any impact on the z coordinates
            and fully shifts all the points on x and y.
          x |  cos(θ), sin(θ),   0,   0 |
          y | -sin(θ), cos(θ),   0,   0 |
          z |      0,      0,    1,   0 |
          w |      0,      0,    0,   1 |
          
          The rotation component sits in the top left of our transformation matrix.
         */
        
        double rads = Math.toRadians(angle);
        Matrix4 zMatrix = new Matrix4();
        float cos = (float) Math.cos(rads);
        float sin = (float) Math.sin(rads);
        
        zMatrix.m00(cos).m01(sin);
        zMatrix.m10(-sin).m11(cos);
        
        return this.mult(zMatrix);
    }
    public Matrix4 scale(float x, float y, float z)
    {
        Matrix4 sMatrix = new Matrix4();
        sMatrix.m00(x).m11(y).m22(z);
        
        return this.mult(sMatrix);
    }
    
    public Matrix4 extractAxis(Vector3 outPositiveX, Vector3 outPositiveY, Vector3 outPositiveZ)
    {
        outPositiveX.set(m00, m01, m02);
        outPositiveY.set(m10, m11, m12);
        outPositiveZ.set(m20, m21, m22);
        return this;
    }
    
    public Matrix4 transpose()
    {
        float n01 = m10();
        float n02 = m20();
        float n03 = m30();
        
        float n10 = m01();
        float n12 = m21();
        float n13 = m31();
        
        float n20 = m02();
        float n21 = m12();
        float n23 = m32();
        
        float n30 = m03();
        float n31 = m13();
        float n32 = m23();
        
        return this.m01(n01).m02(n02).m03(n03)
                .m10(n10).m12(n12).m13(n13)
                .m20(n20).m21(n21).m23(n23)
                .m30(n30).m31(n31).m32(n32);
    }
    
    public Matrix4 copy(Matrix4 destination)
    {
        destination.m00 = m00;
        destination.m01 = m01;
        destination.m02 = m02;
        destination.m03 = m03;
        destination.m10 = m10;
        destination.m11 = m11;
        destination.m12 = m12;
        destination.m13 = m13;
        destination.m20 = m20;
        destination.m21 = m21;
        destination.m22 = m22;
        destination.m23 = m23;
        destination.m30 = m30;
        destination.m31 = m31;
        destination.m32 = m32;
        destination.m33 = m33;
        return this;
    }
    
    public Matrix4 fill(float value)
    {
        m00 = value;
        m01 = value;
        m02 = value;
        m03 = value;
        
        m10 = value;
        m11 = value;
        m12 = value;
        m13 = value;
        
        m20 = value;
        m21 = value;
        m22 = value;
        m23 = value;
        
        m30 = value;
        m31 = value;
        m32 = value;
        m33 = value;
        
        return this;
    }
    
    public Matrix4 identity()
    {
        return set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    public Matrix4 set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        return this;
    }
    
    public Matrix4 mult(float scalar)
    {
        m00 *= scalar;
        m01 *= scalar;
        m02 *= scalar;
        m03 *= scalar;
        
        m10 *= scalar;
        m11 *= scalar;
        m12 *= scalar;
        m13 *= scalar;
        
        m20 *= scalar;
        m21 *= scalar;
        m22 *= scalar;
        m23 *= scalar;
        
        m30 *= scalar;
        m31 *= scalar;
        m32 *= scalar;
        m33 *= scalar;
        
        return this;
    }
    
    public Matrix4 mult(Matrix4 matrix)
    {
        //Math.fma uses a specific cpu instruction that reduces rounding error by combining a multiplication and an addition into 1 error.
        float n00 = Math.fma(m00(), matrix.m00(), Math.fma(m01(), matrix.m10(), Math.fma(m02(), matrix.m20(), m03() * matrix.m30())));
        float n01 = Math.fma(m00(), matrix.m01(), Math.fma(m01(), matrix.m11(), Math.fma(m02(), matrix.m21(), m03() * matrix.m31())));
        float n02 = Math.fma(m00(), matrix.m02(), Math.fma(m01(), matrix.m12(), Math.fma(m02(), matrix.m22(), m03() * matrix.m32())));
        float n03 = Math.fma(m00(), matrix.m03(), Math.fma(m01(), matrix.m13(), Math.fma(m02(), matrix.m23(), m03() * matrix.m33())));
        
        float n10 = Math.fma(m10(), matrix.m00(), Math.fma(m11(), matrix.m10(), Math.fma(m12(), matrix.m20(), m13() * matrix.m30())));
        float n11 = Math.fma(m10(), matrix.m01(), Math.fma(m11(), matrix.m11(), Math.fma(m12(), matrix.m21(), m13() * matrix.m31())));
        float n12 = Math.fma(m10(), matrix.m02(), Math.fma(m11(), matrix.m12(), Math.fma(m12(), matrix.m22(), m13() * matrix.m32())));
        float n13 = Math.fma(m10(), matrix.m03(), Math.fma(m11(), matrix.m13(), Math.fma(m12(), matrix.m23(), m13() * matrix.m33())));
    
        float n20 = Math.fma(m20(), matrix.m00(), Math.fma(m21(), matrix.m10(), Math.fma(m22(), matrix.m20(), m23() * matrix.m30())));
        float n21 = Math.fma(m20(), matrix.m01(), Math.fma(m21(), matrix.m11(), Math.fma(m22(), matrix.m21(), m23() * matrix.m31())));
        float n22 = Math.fma(m20(), matrix.m02(), Math.fma(m21(), matrix.m12(), Math.fma(m22(), matrix.m22(), m23() * matrix.m32())));
        float n23 = Math.fma(m20(), matrix.m03(), Math.fma(m21(), matrix.m13(), Math.fma(m22(), matrix.m23(), m23() * matrix.m33())));
    
        float n30 = Math.fma(m30(), matrix.m00(), Math.fma(m31(), matrix.m10(), Math.fma(m32(), matrix.m20(), m33() * matrix.m30())));
        float n31 = Math.fma(m30(), matrix.m01(), Math.fma(m31(), matrix.m11(), Math.fma(m32(), matrix.m21(), m33() * matrix.m31())));
        float n32 = Math.fma(m30(), matrix.m02(), Math.fma(m31(), matrix.m12(), Math.fma(m32(), matrix.m22(), m33() * matrix.m32())));
        float n33 = Math.fma(m30(), matrix.m03(), Math.fma(m31(), matrix.m13(), Math.fma(m32(), matrix.m23(), m33() * matrix.m33())));

        m00 = n00;
        m01 = n01;
        m02 = n02;
        m03 = n03;
    
        m10 = n10;
        m11 = n11;
        m12 = n12;
        m13 = n13;
    
        m20 = n20;
        m21 = n21;
        m22 = n22;
        m23 = n23;
    
        m30 = n30;
        m31 = n31;
        m32 = n32;
        m33 = n33;
        
        return this;
    }
    
    public Vector4 mult(Vector4 vector)
    {
        return mult(vector, vector);
    }
    public Vector4 mult(Vector4 vector, Vector4 dest)
    {
        float nX = vector.x() * m00() + vector.y() * m01() + vector.z() * m02() + vector.w() * m03();
        float nY = vector.x() * m10() + vector.y() * m11() + vector.z() * m12() + vector.w() * m13();
        float nZ = vector.x() * m20() + vector.y() * m21() + vector.z() * m22() + vector.w() * m23();
        float nW = vector.x() * m30() + vector.y() * m31() + vector.z() * m32() + vector.w() * m33();
        return dest.set(nX, nY, nZ, nW);
    }

    public Matrix4 positiveAxis(Vector3 rightDest, Vector3 upDest, Vector3 forwardDest)
    {
        Vector3 scale = new Vector3(new Vector3(m00(), m10(), m20()).norm(), new Vector3(m01(), m11(), m21()).norm(), new Vector3(m02(), m12(), m22()).norm());
        
        rightDest.set(m00() / scale.x(), m10() / scale.x(), m20() / scale.x());
        upDest.set(m01() / scale.y(), m11() / scale.y(), m21() / scale.y());
        forwardDest.set(m02() / scale.z(), m12() / scale.z(), m22() / scale.z());
        
        return this;
    }
    
    public Vector3 positiveX(Vector3 destination)
    {
        return destination.set(this.m11() * this.m22() - this.m21() * this.m12(), this.m20() * this.m12() - this.m10() * this.m22(), this.m10() * this.m21() - this.m20() * this.m11()).normalize();
    }
    public Vector3 positiveY(Vector3 destination)
    {
        return destination.set(this.m21() * this.m02() - this.m01() * this.m22(), this.m00() * this.m22() - this.m20() * this.m02(), this.m20() * this.m01() - this.m00() * this.m21()).normalize();
    }
    
    public Vector3 positiveZ(Vector3 destination)
    {
        return destination.set(this.m01() * this.m12() - this.m11() * this.m02(), this.m02() * this.m10() - this.m12() * this.m00(), this.m00() * this.m11() - this.m10() * this.m01()).normalize();
    }
    
    public FloatBuffer get(FloatBuffer buffer)
    {
        buffer.put(0, m00()).put(1, m10()).put(2, m20()).put(3, m30())
                .put(4, m01()).put(5, m11()).put(6, m21()).put(7, m31())
                .put(8, m02()).put(9, m12()).put(10, m22()).put(11, m32())
                .put(12, m03()).put(13, m13()).put(14, m23()).put(15, m33());
        return buffer;
    }
    
    @Override
    public int getRows()
    {
        return 4;
    }
    
    @Override
    public int getColumns()
    {
        return 4;
    }
    
    
    public Matrix4 m00(float m00)
    {
        this.m00 = m00;
        return this;
    }
    
    public Matrix4 m01(float m01)
    {
        this.m01 = m01;
        return this;
    }
    
    public Matrix4 m02(float m02)
    {
        this.m02 = m02;
        return this;
    }
    
    public Matrix4 m03(float m03)
    {
        this.m03 = m03;
        return this;
    }
    
    public Matrix4 m10(float m10)
    {
        this.m10 = m10;
        return this;
    }
    
    public Matrix4 m11(float m11)
    {
        this.m11 = m11;
        return this;
    }
    
    public Matrix4 m12(float m12)
    {
        this.m12 = m12;
        return this;
    }
    
    public Matrix4 m13(float m13)
    {
        this.m13 = m13;
        return this;
    }
    
    public Matrix4 m20(float m20)
    {
        this.m20 = m20;
        return this;
    }
    
    public Matrix4 m21(float m21)
    {
        this.m21 = m21;
        return this;
    }
    
    public Matrix4 m22(float m22)
    {
        this.m22 = m22;
        return this;
    }
    
    public Matrix4 m23(float m23)
    {
        this.m23 = m23;
        return this;
    }
    
    public Matrix4 m30(float m30)
    {
        this.m30 = m30;
        return this;
    }
    
    public Matrix4 m31(float m31)
    {
        this.m31 = m31;
        return this;
    }
    
    public Matrix4 m32(float m32)
    {
        this.m32 = m32;
        return this;
    }
    
    public Matrix4 m33(float m33)
    {
        this.m33 = m33;
        return this;
    }
    
    public float m00()
    {
        return m00;
    }
    
    public float m01()
    {
        return m01;
    }
    
    public float m02()
    {
        return m02;
    }
    
    public float m03()
    {
        return m03;
    }
    
    public float m10()
    {
        return m10;
    }
    
    public float m11()
    {
        return m11;
    }
    
    public float m12()
    {
        return m12;
    }
    
    public float m13()
    {
        return m13;
    }
    
    public float m20()
    {
        return m20;
    }
    
    public float m21()
    {
        return m21;
    }
    
    public float m22()
    {
        return m22;
    }
    
    public float m23()
    {
        return m23;
    }
    
    public float m30()
    {
        return m30;
    }
    
    public float m31()
    {
        return m31;
    }
    
    public float m32()
    {
        return m32;
    }
    
    public float m33()
    {
        return m33;
    }
    
    @Override
    public String toString()
    {
        return "{[" + m00() + ", " + m01() + ", " + m02() + ", " + m03() + "]\n [" +
                m10() + ", " + m11() + ", " + m12() + ", " + m13() + "]\n [" +
                m20() + ", " + m21() + ", " + m22() + ", " + m23() + "]\n [" +
                m30() + ", " + m31() + ", " + m32() + ", " + m33() + "]}";
    }
}
