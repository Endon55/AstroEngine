package com.anthonycosenza.engine.space.entity;


public class PlaneMesh extends GeneratedMesh
{
    private int quadsWide = 10;
    private int quadsDeep = 10;
    private float quadWidth = 1;
    private float quadDepth = 1;
    
    public void setPlane(int quadsWide, int quadsDeep, float quadWidth, float quadDepth)
    {
        this.quadsWide = quadsWide;
        this.quadsDeep = quadsDeep;
        this.quadWidth = quadWidth;
        this.quadDepth = quadDepth;
        initialize();
    }
    
    @Override
    public void initialize()
    {
        if(quadsWide < 1 || quadsDeep < 1 || quadWidth == -1 || quadDepth == -1) throw new RuntimeException("Plane Mesh not properly initialized");
        
        int vertsWide = quadsWide + 1;
        int vertsDeep = quadsDeep + 1;
        
        float[] vertices = new float[(vertsWide * vertsDeep) * 3];
        int[] indices = new int[quadsWide * quadsDeep * 6]; //2 triangles per quad, 3 verts per triangle
        float[] textureCoordinates = new float[(vertsWide * vertsDeep) * 2];
        
        float planeWidth = quadsWide * quadWidth;
        float planeDepth = quadsDeep * quadDepth;
        
        float planeWidth2 = planeWidth * .5f;
        float planeDepth2 = planeDepth * .5f;
        
        int index = 0;
        for(int i = 0; i < vertsWide; i++)
        {
            float x = (quadWidth * i) - planeWidth2;
            for(int j = 0; j < vertsDeep; j++)
            {
                float z = (quadDepth * j) - planeDepth2;
                vertices[index++] = x;
                vertices[index++] = 0;
                vertices[index++] = z;
            }
        }
        index = 0;
        int thisVert = 0;
        int nextVert = 0;
        for(int i = 0; i < quadsDeep; i++)
        {
            thisVert = i * vertsWide;
            nextVert = (i + 1) * vertsWide;
            
            for(int j = 0; j < quadsWide; j++)
            {
                //First Triangle
                indices[index++] = thisVert;
                indices[index++] = thisVert + 1;
                indices[index++] = nextVert + 1;
                //Second Triangle
                indices[index++] = thisVert;
                indices[index++] = nextVert + 1;
                indices[index++] = nextVert;
                thisVert++;
                nextVert++;
            }
        }
        
        index = 0;
        float widthWeight = quadWidth / planeWidth;
        float depthWeight = quadDepth / planeDepth;
        for(int i = 0; i < vertsWide; i++)
        {
            for(int j = 0; j < vertsDeep; j++)
            {
                textureCoordinates[index++] = i * widthWeight;
                textureCoordinates[index++] = j * depthWeight;
            }
        }
        set(vertices, indices, textureCoordinates);
    }
    
    
    public int getQuadsWide()
    {
        return quadsWide;
    }
    
    public void setQuadsWide(int quadsWide)
    {
        this.quadsWide = quadsWide;
        initialize();
    }
    
    public int getQuadsDeep()
    {
        return quadsDeep;
    }
    
    public void setQuadsDeep(int quadsDeep)
    {
        this.quadsDeep = quadsDeep;
        initialize();
    }
    
    public float getQuadWidth()
    {
        return quadWidth;
    }
    
    public void setQuadWidth(float quadWidth)
    {
        this.quadWidth = quadWidth;
        initialize();
    }
    
    public float getQuadDepth()
    {
        return quadDepth;
    }
    
    public void setQuadDepth(float quadDepth)
    {
        this.quadDepth = quadDepth;
        initialize();
    }
    
}
