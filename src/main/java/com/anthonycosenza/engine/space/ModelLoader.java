package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.entity.texture.Material;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.util.math.Color;
import com.anthonycosenza.engine.util.math.matrix.Matrix4;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

public class ModelLoader
{
    /*
     * Set triangulate flag which converts all faces into triangles.
     */
    public static Model loadModel(String modelPath, int flags)
    {
        flags |= Assimp.aiProcess_Triangulate;
        File file = new File(modelPath);
        if(!file.exists())
        {
            throw new RuntimeException("File doesn't exist.");
        }
        String parentDirectory = file.getParent();
        AIScene aiScene = aiImportFile(modelPath, flags);
        if(aiScene == null)
        {
            throw new RuntimeException("Assimp failed to load model.");
        }
        List<Material> materials = new ArrayList<>();
        int numMaterials = aiScene.mNumMaterials();
        for(int i = 0; i < numMaterials; i++)
        {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materials.add(processMaterial(aiMaterial, parentDirectory));
        }
        PointerBuffer mMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();
        for(int i = 0; i < aiScene.mNumMeshes(); i++)
        {
            AIMesh aiMesh = AIMesh.create(mMeshes.get(i));
            Mesh mesh = createMesh(aiMesh);
            int materialIndex = aiMesh.mMaterialIndex();
            Material material;
            if(materialIndex >= 0 && materialIndex < materials.size())
            {
                material = materials.get(materialIndex);
            }
            else material = defaultMaterial;
            material.getMeshes().add(mesh);
        }
        if(!defaultMaterial.getMeshes().isEmpty())
        {
            materials.add(defaultMaterial);
        }
        return new Model(materials);
    }
    
    private static Material processMaterial(AIMaterial aiMaterial, String parentDirectory)
    {
        Material material = new Material();
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            AIColor4D color = AIColor4D.create();
            
            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
            {
                material.setDiffuseColor(new Color(color.r(), color.g(), color.b(), color.a()));
            }
            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,
                    null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            if(texturePath != null && texturePath.length() > 0)
            {
                material.setTexture(parentDirectory + File.separator + new File(texturePath).getName());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }
        }
        return material;
    }
    
    
    private static Mesh createMesh(AIMesh aiMesh)
    {
        float[] vertices = extractVertices(aiMesh);
        //float[] normals = extractNormals(aiMesh);
        float[] textureCoordinates = extractTextureCoordinates(aiMesh);
        int[] indices = extractIndices(aiMesh);
        
        return new Mesh(vertices, indices, textureCoordinates);
    }
    
    private static float[] extractVertices(AIMesh aiMesh)
    {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] vertices = new float[buffer.remaining() * 3];//3 values per vertex
        int pointer = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D vertex = buffer.get();
            vertices[pointer++] = vertex.x();
            vertices[pointer++] = vertex.y();
            vertices[pointer++] = vertex.z();
        }
        return vertices;
    }
    
    /*
     * Textures are optional
     */
    private static float[] extractTextureCoordinates(AIMesh aiMesh)
    {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        
        if(buffer == null) return new float[]{};
        
        float[] coordinates = new float[buffer.remaining() * 2];//2 values per texture coordinate
        
        int pointer = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D coordinate = buffer.get();
            coordinates[pointer++] = coordinate.x();
            coordinates[pointer++] = 1 - coordinate.y();
        }
        return coordinates;
    }
    
    /*
     * Indices are how we create the triangles, a triangle consists of 3 vertices, to save space
     * we only define a vertex once and reference it multiple times using its index.
     * Indices are 3 values that each correspond to an index in the vertex array.
     *
     * A face can be any sized polygon, we force the triangulation flag so that it's a proper triangle.
     *
     */
    private static int[] extractIndices(AIMesh aiMesh)
    {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        int[] indices = new int[numFaces * 3];
        int pointer = 0;
        for(int i = 0; i < numFaces; i++)
        {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while(buffer.remaining() > 0)
            {
                indices[pointer++] = buffer.get();
            }
        }
        return indices;
    }
    
}
