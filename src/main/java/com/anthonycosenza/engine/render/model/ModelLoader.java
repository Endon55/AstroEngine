package com.anthonycosenza.engine.render.model;

import com.anthonycosenza.engine.Utils;
import com.anthonycosenza.engine.render.TextureCache;
import com.anthonycosenza.engine.render.model.animation.AnimatedFrame;
import com.anthonycosenza.engine.render.model.animation.Animation;
import com.anthonycosenza.engine.render.model.animation.AnimationMeshData;
import com.anthonycosenza.engine.render.model.animation.Bone;
import com.anthonycosenza.engine.render.model.animation.Node;
import com.anthonycosenza.engine.render.model.animation.VertexWeight;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAABB;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS_STRENGTH;
import static org.lwjgl.assimp.Assimp.aiGetMaterialFloatArray;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenBoundingBoxes;
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
    public static final int MAX_BONES = 150;
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    
    
    
    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, boolean animation)
    {
        //More flags can be found at https://javadoc.lwjgl.org/org/lwjgl/assimp/Assimp.html
        return loadModel(modelId, modelPath, textureCache,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                       aiProcess_GenBoundingBoxes | (animation ? 0 : aiProcess_PreTransformVertices));
        
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
        List<Bone> boneList = new ArrayList<>();
        for(int i = 0; i < numMeshes; i++)
        {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, boneList);
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
        
        List<Animation> animations = new ArrayList<>();
        int numAnimations = aiScene.mNumAnimations();
        if(numAnimations > 0)
        {
            Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
            Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
            animations = processAnimations(aiScene, boneList, rootNode, globalInverseTransformation);
        }
        Assimp.aiReleaseImport(aiScene);
        
        return new Model(modelId, materialList, animations);
    }
    
    private static Node buildNodesTree(AINode aiNode, Node parentNode)
    {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode, toMatrix(aiNode.mTransformation()));
        
        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for(int i = 0; i < numChildren; i++)
        {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = buildNodesTree(aiChildNode, node);
            node.addChild(childNode);
        }
        return node;
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
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath,
                    (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            
            if(texturePath != null && texturePath.length() > 0)
            {
                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }
            
            AIString aiNormalMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath,
                    (IntBuffer) null, null, null, null, null ,null);
            String normalMapPath = aiNormalMapPath.dataString();
            if(normalMapPath != null && normalMapPath.length() > 0)
            {
                material.setNormalMapPath(modelDir + File.separator + new File(normalMapPath).getName());
                textureCache.createTexture(material.getNormalMapPath());
            }
        }
        return material;
    }
    private static Mesh processMesh(AIMesh aiMesh, List<Bone> boneList)
    {
        float[] vertices = processVertices(aiMesh);
        float[] textureCoords = processTextureCoords(aiMesh);
        float[] normals = processNormals(aiMesh);
        float[] tangents = processTangents(aiMesh, normals);
        float[] bitangents = processBitangents(aiMesh, normals);
        int[] indices = processIndices(aiMesh);
        AnimationMeshData animationMeshData = processBones(aiMesh, boneList);
        
        if(textureCoords.length == 0)
        {
            //No default texture initializes the array with empty values.
            System.out.println("No texture, using default");
            int numElements = (vertices.length / 3) * 2;
            textureCoords = new float[numElements];
        }
        
        AIAABB aabb = aiMesh.mAABB();
        Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
        Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());
        return new Mesh(vertices, normals, tangents, bitangents, textureCoords,
                indices, animationMeshData.boneIds(), animationMeshData.weights(), aabbMin, aabbMax);
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
    
    private static List<Animation> processAnimations(AIScene aiScene, List<Bone> boneList, Node rootNode, Matrix4f globalInverseTransformation)
    {
        List<Animation> animations = new ArrayList<>();
        
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for(int i = 0; i < numAnimations; i++)
        {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int maxFrames = calculateAnimationMaxFrames(aiAnimation);
            
            List<AnimatedFrame> frames = new ArrayList<>();
            Animation animation = new Animation(aiAnimation.mName().dataString(), aiAnimation.mDuration(), frames);
            animations.add(animation);
    
            for(int j = 0; j < maxFrames; j++)
            {
                Matrix4f[] bonesMatrices = new Matrix4f[MAX_BONES];
                Arrays.fill(bonesMatrices, IDENTITY_MATRIX);
                AnimatedFrame animatedFrame = new AnimatedFrame(bonesMatrices);
                buildFrameMatrices(aiAnimation, boneList, animatedFrame, j, rootNode, rootNode.getNodeTransformation(), globalInverseTransformation);
                frames.add(animatedFrame);
            }
        }
        return animations;
    }
    
    private static void buildFrameMatrices(AIAnimation aiAnimation, List<Bone> boneList, AnimatedFrame animatedFrame, int frame, Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransformation)
    {
        String nodeName = node.getName();
        AINodeAnim aiNodeAnim = findAIAnimNode(aiAnimation, nodeName);
        Matrix4f nodeTransform = node.getNodeTransformation();
        if(aiNodeAnim != null)
        {
            nodeTransform = buildNodeTransformationMatrix(aiNodeAnim, frame);
        }
        Matrix4f nodeGlobalTransform = new Matrix4f(parentTransformation).mul(nodeTransform);
        
        List<Bone> affectedBones = boneList.stream().filter(b -> b.boneName().equals(nodeName)).toList();
        for(Bone bone : affectedBones)
        {
            Matrix4f boneTransform = new Matrix4f(globalInverseTransformation).mul(nodeGlobalTransform).mul(bone.offsetMatrix());
            animatedFrame.boneMatrices()[bone.boneId()] = boneTransform;
        }
        for(Node childNode : node.getChildren())
        {
            buildFrameMatrices(aiAnimation, boneList, animatedFrame, frame, childNode, nodeGlobalTransform, globalInverseTransformation);
        }
    }
    
    private static Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame)
    {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();
        
        AIVectorKey aiVectorKey;
        AIVector3D vec;
        
        Matrix4f nodeTransform = new Matrix4f();
        int numPositions = aiNodeAnim.mNumPositionKeys();
        if(numPositions > 0)
        {
            aiVectorKey = positionKeys.get(Math.min(numPositions - 1, frame));
            vec = aiVectorKey.mValue();
            nodeTransform.translate(vec.x(), vec.y(), vec.z());
        }
        int numRotations = aiNodeAnim.mNumRotationKeys();
        if(numRotations > 0)
        {
            AIQuatKey quatKey = rotationKeys.get(Math.min(numRotations - 1, frame));
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            nodeTransform.rotate(quat);
        }
        int numScalingKeys = aiNodeAnim.mNumScalingKeys();
        if(numScalingKeys > 0)
        {
            aiVectorKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
            vec = aiVectorKey.mValue();
            nodeTransform.scale(vec.x(), vec.y(), vec.z());
        }
        return nodeTransform;
    }
    
    private static AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName)
    {
        AINodeAnim result = null;
        int numAnimNodes = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for(int i = 0; i < numAnimNodes; i++)
        {
            AINodeAnim aiNodeANim = AINodeAnim.create(aiChannels.get(i));
            if(nodeName.equals(aiNodeANim.mNodeName().dataString()))
            {
                result = aiNodeANim;
                break;
            }
        }
        return result;
    }
    
    private static int calculateAnimationMaxFrames(AIAnimation aiAnimation)
    {
        int maxFrames = 0;
        int numNodeAnimations = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for(int i = 0; i < numNodeAnimations; i++)
        {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()), aiNodeAnim.mNumRotationKeys());
            maxFrames = Math.max(maxFrames, numFrames);
        }
        return maxFrames;
    }
    
    private static AnimationMeshData processBones(AIMesh aiMesh, List<Bone> boneList)
    {
        List<Integer> boneIds = new ArrayList<>();
        List<Float> weights = new ArrayList<>();
        
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        
        for(int i = 0; i < numBones; i++)
        {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for(int j = 0; j < numWeights; j++)
            {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.boneId(), aiWeight.mVertexId(), aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.get(vw.vertexId());
                if(vertexWeightList == null)
                {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.vertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }
        
        int numVertices = aiMesh.mNumVertices();
        for(int i = 0; i < numVertices; i++)
        {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for(int j = 0; j < Mesh.MAX_WEIGHTS; j++)
            {
                if(j < size)
                {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.weight());
                    boneIds.add(vw.boneId());
                }
                else
                {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }
        return new AnimationMeshData(Utils.listFloatToArray(weights), Utils.listIntToArray(boneIds));
    }
    private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix)
    {
        Matrix4f result = new Matrix4f();
        
        result.m00(aiMatrix.a1());
        result.m10(aiMatrix.a2());
        result.m20(aiMatrix.a3());
        result.m30(aiMatrix.a4());
        result.m01(aiMatrix.b1());
        result.m11(aiMatrix.b2());
        result.m21(aiMatrix.b3());
        result.m31(aiMatrix.b4());
        result.m02(aiMatrix.c1());
        result.m12(aiMatrix.c2());
        result.m22(aiMatrix.c3());
        result.m32(aiMatrix.c4());
        result.m03(aiMatrix.d1());
        result.m13(aiMatrix.d2());
        result.m23(aiMatrix.d3());
        result.m33(aiMatrix.d4());
        
        return result;
    }
    
}
