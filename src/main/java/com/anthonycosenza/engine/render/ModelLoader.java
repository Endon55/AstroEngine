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

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
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
            
            int result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if(result == aiReturn_SUCCESS)
            {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }
            
            AIString aiTexturePath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            
            if(texturePath != null && texturePath.length() > 0)
            {
                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }
        }
        return material;
    }
    private static Mesh processMesh(AIMesh aiMesh)
    {
        float[] vertices = processVertices(aiMesh);
        float[] textureCoords = processTextureCoords(aiMesh);
        int[] indices = processIndices(aiMesh);
        if(textureCoords.length == 0)
        {
            //No default texture initializes the arry with empty values.
            int numElements = (vertices.length / 3) * 2;
            textureCoords = new float[numElements];
        }
        return new Mesh(vertices, textureCoords, indices);
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
