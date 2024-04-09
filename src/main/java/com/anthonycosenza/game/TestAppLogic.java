package com.anthonycosenza.game;

import com.anthonycosenza.engine.MouseInput;
import com.anthonycosenza.engine.game.IAppLogic;
import com.anthonycosenza.engine.render.light.AmbientLight;
import com.anthonycosenza.engine.render.model.Material;
import com.anthonycosenza.engine.render.model.Mesh;
import com.anthonycosenza.engine.render.model.Model;
import com.anthonycosenza.engine.render.model.ModelLoader;
import com.anthonycosenza.engine.render.Render;
import com.anthonycosenza.engine.render.light.DirectionalLight;
import com.anthonycosenza.engine.render.light.SceneLighting;
import com.anthonycosenza.engine.render.model.animation.AnimationData;
import com.anthonycosenza.engine.scene.Camera;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Fog;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.scene.SkyBox;
import com.anthonycosenza.engine.sound.SoundBuffer;
import com.anthonycosenza.engine.sound.SoundListener;
import com.anthonycosenza.engine.sound.SoundManager;
import com.anthonycosenza.engine.sound.SoundSource;
import com.anthonycosenza.engine.window.Window;
import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL11;

import java.util.Collection;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class TestAppLogic implements IAppLogic
{
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.1f;
    private AnimationData animationData;
    private float lightAngle;
    private SoundSource playerSoundSource;
    private SoundManager soundManager;
    Entity cubeEntity1;
    Entity cubeEntity2;
    private float rotation;
    
    @Override
    public void init(Window window, Scene scene, Render render)
    {
        String terrainModelId = "terrain";
        Model terrainModel = ModelLoader.loadModel(terrainModelId, "resources/models/terrain/terrain.obj",
                scene.getTextureCache(), false);
        scene.addModel(terrainModel);
        Entity terrainEntity = new Entity("terrainEntity", terrainModelId);
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);
    
        String bobModelId = "bobModel";
        Model bobModel = ModelLoader.loadModel(bobModelId, "resources/models/bob/boblamp.md5mesh",
                scene.getTextureCache(), true);
        scene.addModel(bobModel);
        Entity bobEntity = new Entity("bobEntity", bobModelId);
        bobEntity.setScale(0.05f);
        bobEntity.updateModelMatrix();
        animationData = new AnimationData(bobModel.getAnimationList().get(0));
        bobEntity.setAnimationData(animationData);
        scene.addEntity(bobEntity);
    
        SceneLighting sceneLights = new SceneLighting();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);
    
        DirectionalLight dirLight = sceneLights.getDirectionalLight();
        dirLight.setDirection(0, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLighting(sceneLights);
    
        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(100);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkyBox(skyBox);
    
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));
    
        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));
    
        lightAngle = 45.001f;
        
        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cube/cube.obj",
                scene.getTextureCache(), false);
        scene.addModel(cubeModel);
        cubeEntity1 = new Entity("cube-entity-1", cubeModel.getId());
        cubeEntity1.setPosition(0, 2, -1);
        scene.addEntity(cubeEntity1);
    
        cubeEntity2 = new Entity("cube-entity-2", cubeModel.getId());
        cubeEntity2.setPosition(-2, 2, -1);
        scene.addEntity(cubeEntity2);
    }
    
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed)
    {
        //We don't want to process the same input command repeatedly, so we leave the call if it's already been used.
        if(inputConsumed)
        {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        
        if(window.isKeyPressed(GLFW_KEY_W))
        {
            camera.moveForward(move);
        }
        else if(window.isKeyPressed(GLFW_KEY_S))
        {
            camera.moveBackwards(move);
        }
        if(window.isKeyPressed(GLFW_KEY_A))
        {
            camera.moveLeft(move);
        }
        else if(window.isKeyPressed(GLFW_KEY_D))
        {
            camera.moveRight(move);
        }
        if(window.isKeyPressed(GLFW_KEY_SPACE))
        {
            camera.moveUp(move);
        }
        else if(window.isKeyPressed(GLFW_KEY_C))
        {
            camera.moveDown(move);
        }
        
        MouseInput mouseInput = window.getMouseInput();
        if(mouseInput.isRightButtonPressed())
        {
            Vector2f displVec = mouseInput.getDisplayVector();
            camera.addRotation((float) -Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) -Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
        if(mouseInput.isLeftButtonPressed())
        {
            selectEntity(window, scene, mouseInput.getCurrentPos());
        }
        
        
        SceneLighting sceneLights = scene.getSceneLighting();
        DirectionalLight dirLight = sceneLights.getDirectionalLight();
        double angRad = Math.toRadians(lightAngle);
        dirLight.getDirection().z = (float) Math.sin(angRad);
        dirLight.getDirection().y = (float) Math.cos(angRad);

    }
    
    @Override
    public void update(Window window, Scene scene, float interval)
    {
       animationData.nextFrame();
        rotation += 1.5;
        if(rotation > 360)
        {
            rotation = 0;
        }
        cubeEntity1.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity1.updateModelMatrix();
    
        cubeEntity2.setRotation(1, 1, 1, (float) Math.toRadians(360 - rotation));
        cubeEntity2.updateModelMatrix();
    }

    private void initSounds(Vector3f position, Camera camera)
    {
        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        soundManager.setListener(new SoundListener(camera.getPosition()));
    
        SoundBuffer buffer = new SoundBuffer("resources/sounds/creak1.ogg");
        soundManager.addSoundBuffer(buffer);
        playerSoundSource = new SoundSource(false, false);
        playerSoundSource.setPosition(position);
        playerSoundSource.setBuffer(buffer.getBufferId());
        soundManager.addSoundSource("CREAK", playerSoundSource);
        
        buffer = new SoundBuffer("resources/sounds/woo_scary.ogg");
        soundManager.addSoundBuffer(buffer);
        SoundSource source = new SoundSource(true, true);
        source.setBuffer(buffer.getBufferId());
        soundManager.addSoundSource("MUSIC", source);
        source.play();
    }
    
    private void selectEntity(Window window, Scene scene, Vector2f mousePos)
    {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        
        float x = (2 * mousePos.x) / windowWidth - 1.0f;
        float y = 1.0f - (2 * mousePos.y)  /windowHeight;
        float z = -1.0f;
        
        Matrix4f invProjMatrix = scene.getProjection().getInvProjMatrix();
        Vector4f mouseDir = new Vector4f(x, y, z, 1.0f);
        mouseDir.mul(invProjMatrix);
        mouseDir.z = -1.0f;
        mouseDir.w = 0.0f;
        
        Matrix4f invViewMatrix = scene.getCamera().getInvViewMatrix();
        mouseDir.mul(invViewMatrix);
        
        Vector4f min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector2f nearFar = new Vector2f();
        
        Entity selectedEntity = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        Vector3f center = scene.getCamera().getPosition();
        
        Collection<Model> models = scene.getModelMap().values();
        Matrix4f modelMatrix = new Matrix4f();
        for(Model model : models)
        {
            List<Entity> entities = model.getEntityList();
            for(Entity entity : entities)
            {
                modelMatrix.translate(entity.getPosition()).scale(entity.getScale());
                for(Material material : model.getMaterialList())
                {
                    for(Mesh mesh : material.getMeshList())
                    {
                        Vector3f aabbMin = mesh.getAabbMin();
                        min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                        min.mul(modelMatrix);
                        Vector3f aabbMax = mesh.getAabbMax();
                        max.set(aabbMax.x, aabbMax.y, aabbMax.z, 1.0f);
                        max.mul(modelMatrix);
                        if(Intersectionf.intersectRayAab(
                                center.x, center.y, center.z,
                                mouseDir.x, mouseDir.y, mouseDir.z,
                                min.x, min.y, min.z, max.x, max.y, max.z, nearFar)
                                && nearFar.x < closestDistance)
                        {
                            closestDistance = nearFar.x;
                            selectedEntity = entity;
                        }
                    }
                }
                modelMatrix.identity();
            }
        }
        scene.setSelectedEntity(selectedEntity);
    }
    
    
    
    @Override
    public void cleanup()
    {
        //Nothing Yet
    }
    
    
    
}
