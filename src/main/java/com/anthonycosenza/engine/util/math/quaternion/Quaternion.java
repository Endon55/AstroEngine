package com.anthonycosenza.engine.util.math.quaternion;

public class Quaternion
{
    private float x;
    private float y;
    private float z;
    private float w;
    
    public Quaternion()
    {
        this(0, 0, 0, 1);
    }
    public Quaternion(Quaternion quat)
    {
        this(quat.x(), quat.y(), quat.z(), quat.w());
    }
    public Quaternion(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    
    
    public float magnitude()
    {
        return (float) Math.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
    }
    
    public Quaternion rotateDegrees(float x, float y, float z, float angle)
    {
        return rotateDegrees(x, y, z, angle, this);
    }
    
    /*
     * qw = cos(0/2)
     * qx = sin(0/2) * cos(0/2)
     * qy = sin(0/2) * cos(0/2)
     * qz = sin(0/2) * cos(0/2)
     */
    
    public Quaternion setRotationDegrees(float x, float y, float z, float angle)
    {
        return setRotationRadians(x, y, z, (float) Math.toRadians(angle));
    }
    
    public Quaternion setRotationRadians(float x, float y, float z, float angle)
    {
        float half = angle / 2.0f;
        float sin = (float) Math.sin(half);
        float length = (Math.fma(x, x, Math.fma(y, y, z * z))) * sin;
        
        this.x = x / length;
        this.y = y / length;
        this.z = z / length;
        this.w = (float) Math.cos(half);
        return this;
    }
    
    public Quaternion rotateDegrees(float x, float y, float z, float angle, Quaternion dest)
    {
        double rads = (Math.toRadians(angle) / 2d);
        float sin = (float) Math.sin(rads);
        float cos = (float) Math.cos(rads);
    
        dest.w(1);
        dest.x(dest.x() * sin);
        dest.y(dest.y() * sin);
        dest.z(dest.z() * sin);
        dest.mult(cos);
        return dest;
    }
    
    public Quaternion slerp(Quaternion quat, float time)
    {
        float cosOmega = this.dot(quat);
        if(cosOmega < 0.0f)
        {
            quat.negate();
        }
        float k0;
        float k1;
        if(cosOmega > 0.9999f)
        {
            k0 = 1.0f - time;
            k1 = time;
        }
        else
        {
            double sinOmega = Math.sqrt(1.0f - cosOmega * cosOmega);
            double omega = Math.atan2(sinOmega, cosOmega);
            double oneOverSinOmega = 1.0f / sinOmega;
            k0 = (float) (Math.sin((1.0f - time) * omega) * oneOverSinOmega);
            k1 = (float) (Math.sin(time  *omega) * oneOverSinOmega);
        }
        float w = w() * k0 + quat.w() * k1;
        float x = x() * k0 + quat.x() * k1;
        float y = y() * k0 + quat.y() * k1;
        float z = z() * k0 + quat.z() * k1;
        
        return new Quaternion(x, y, z, w);
    }
    
    public float dot(Quaternion quat)
    {
        return Math.fma(w(), quat.w(), Math.fma(x(), quat.x(), Math.fma(y(), quat.y(), z() * quat.z())));
    }
    
    public Quaternion mult(double scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }
    public Quaternion mult(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }
    /*
     * Multiplying 2 unit quaternions returns another unit quaternion.
     */
    public Quaternion mult(Quaternion quat)
    {
        float w2 = Math.fma(w(), quat.w(), -Math.fma(x(), quat.x(), -Math.fma(y(), quat.y(), -z() * quat.z())));
        float x2 = Math.fma(w(), quat.x(),  Math.fma(x(), quat.w(),  Math.fma(y(), quat.z(), -z() * quat.y())));
        float y2 = Math.fma(w(), quat.y(),  Math.fma(y(), quat.w(),  Math.fma(z(), quat.x(), -x() * quat.z())));
        float z2 = Math.fma(w(), quat.z(),  Math.fma(z(), quat.w(),  Math.fma(x(), quat.y(), -y() * quat.x())));
        return this;
    }
    
    public Quaternion conjugate()
    {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }
    
    public Quaternion negate()
    {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }
    
    public Quaternion set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }
    
    public float x()
    {
        return x;
    }
    
    public float y()
    {
        return y;
    }
    
    public float z()
    {
        return z;
    }
    
    public float w()
    {
        return w;
    }
    
    
    public Quaternion x(float x)
    {
        this.x = x;
        return this;
    }
    
    public Quaternion y(float y)
    {
        this.y = y;
        return this;
    }
    
    public Quaternion z(float z)
    {
        this.z = z;
        return this;
    }
    
    public Quaternion w(float w)
    {
        this.w = w;
        return this;
    }
}
