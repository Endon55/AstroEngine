package com.anthonycosenza.engine.render;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS_STRENGTH;
import static org.lwjgl.assimp.Assimp.aiGetMaterialFloatArray;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;
import static org.lwjgl.assimp.Assimp.aiProcess_PreTransformVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NORMALS;

public class ModelLoader
{
    
    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache)
    {
        //More flags can be found at https://javadoc.lwjgl.org/org/lwjgl/assimp/Assimp.html
        return loadModel(modelId, modelPath, textureCache,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);
        
    }
    
    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, int flags)
    {
        File file = new File(modelPath);
        if(!file.exists())
        {
            throw new RuntimeException("Model does not exist at path[" + modelPath + "]");
        }
        String modelDir = file.getParent();
        AIScene aiScene = Assimp.aiImportFile(modelPath, flags);
        if(aiScene == null)
        {
            throw new RuntimeException("Error loading Model at path[" + modelPath + "]");
        }
        int numMaterials = aiScene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();
        for(int i = 0; i < numMaterials; i++)
        {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, modelDir, textureCache));
        }
        
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();
        for(int i = 0; i < numMeshes; i++)
        {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            int materialIndex = aiMesh.mMaterialIndex();
            Material material;
            if(materialIndex >= 0 && materialIndex < materialList.size())
            {
                material = materialList.get(materialIndex);
            }
            else material = defaultMaterial;
            
            material.getMeshList().add(mesh);
        }
        
        if(!defaultMaterial.getMeshList().isEmpty())
        {
            materialList.add(defaultMaterial);
        }
        return new Model(modelId, materialList);
    }
    
    private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache)
    {
        Material material = new Material();
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            AIColor4D color = AIColor4D.create();
            int result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
            {
                material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }
            result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
            {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }
            result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
            {
                material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }
            
            float reflectance = 0.0f;
            float[] shininessFactor = new float[]{0.0f};
            int[] pMax = new int[]{1};
            result = Assimp.aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFactor, pMax);
            if(result != aiReturn_SUCCESS)
            {
                reflectance = shininessFactor[0];
            }
            material.setReflectance(reflectance);
            
            
            AIString aiTexturePath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            
            if(texturePath != null && texturePath.length() > 0)
            {
                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }
            
            AIString aiNormalMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath, (IntBuffer) null, null, null, null, null ,null);
            String normalMapPath = aiNormalMapPath.dataString();
            if(normalMapPath != null && normalMapPath.length() > 0)
            {
                material.setNormalMapPath(modelDir + File.separator + new File(normalMapPath).getName());
                textureCache.createTexture(material.getNormalMapPath());
            }
        }
        return material;
    }
    private static Mesh processMesh(AIMesh aiMesh)
    {
        float[] vertices = processVertices(aiMesh);
        float[] textureCoords = processTextureCoords(aiMesh);
        float[] normals = processNormals(aiMesh);
        float[] tangents = processTangents(aiMesh, normals);
        float[] bitangents = processBitangents(aiMesh, normals);
        int[] indices = processIndices(aiMesh);
        if(textureCoords.length == 0)
        {
            //No default texture initializes the arry with empty values.
            int numElements = (vertices.length / 3) * 2;
            textureCoords = new float[numElements];
        }
        return new Mesh(vertices, normals, tangents, bitangents, textureCoords, indices);
    }
    
    private static float[] processVertices(AIMesh aiMesh)
    {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int position = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D textureCoordinate = buffer.get();
            data[position++] = textureCoordinate.x();
            data[position++] = textureCoordinate.y();
            data[position++] = textureCoordinate.z();
        }
        return data;
    }
    
    
    private static float[] processTextureCoords(AIMesh aiMesh)
    {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if(buffer == null)
        {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int position = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D textureCoordinate = buffer.get();
            data[position++] = textureCoordinate.x();
            data[position++] = 1 - textureCoordinate.y();
        }
        return data;
    }
    
    private static float[] processNormals(AIMesh aiMesh)
    {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }
    
    private static float[] processTangents(AIMesh aiMesh, float[] normals)
    {
        AIVector3D.Buffer buffer = aiMesh.mTangents();
        assert buffer != null;
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D aiTangent = buffer.get();
            data[pos++] = aiTangent.x();
            data[pos++] = aiTangent.y();
            data[pos++] = aiTangent.z();
        }
        
        if(data.length == 0)
        {
            data = new float[normals.length];
        }
        
        return data;
    }
    
    private static float[] processBitangents(AIMesh aiMesh, float[] normals)
    {
        AIVector3D.Buffer buffer = aiMesh.mBitangents();
        assert buffer != null;
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while(buffer.remaining() > 0)
        {
            AIVector3D aiBitangent = buffer.get();
            data[pos++] = aiBitangent.x();
            data[pos++] = aiBitangent.y();
            data[pos++] = aiBitangent.z();
        }
        
        if(data.length == 0)
        {
            data = new float[normals.length];
        }
        
        return data;
    }
    
    
    private static int[] processIndices(AIMesh aiMesh)
    {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for(int i = 0; i < numFaces; i++)
        {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while(buffer.remaining() > 0)
            {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
    
    
}
