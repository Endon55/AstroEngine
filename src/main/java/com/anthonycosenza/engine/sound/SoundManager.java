package com.anthonycosenza.engine.sound;

import com.anthonycosenza.engine.scene.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

public class SoundManager
{
    private final List<SoundBuffer> soundBufferList;
    private final Map<String, SoundSource> soundSourceMap;
    private long context;
    private long device;
    private SoundListener listener;
    
    public SoundManager()
    {
        soundBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();
        
        device = alcOpenDevice((ByteBuffer) null);
        if(device == MemoryUtil.NULL)
        {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if(context == MemoryUtil.NULL)
        {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCapabilities);
    }
    
    public void playSoundSource(String name)
    {
        SoundSource soundSource = this.soundSourceMap.get(name);
        if(soundSource == null)
        {
            throw new RuntimeException("Sound Source not found:" + name);
        }
        if(soundSource.isPlaying())
        {
            soundSource.play();
        }
    }
    
    public void updateListenerPosition(Camera camera)
    {
        Matrix4f viewMatrix = camera.getViewMatrix();
        listener.setPosition(camera.getPosition());
        Vector3f at = new Vector3f();
        viewMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        viewMatrix.positiveY(up);
        
        listener.setOrientation(at, up);
    }
    
    public void setAttenuationModel(int model)
    {
        alDistanceModel(model);
    }
    
    public SoundSource getSoundSource(String name)
    {
        return this.soundSourceMap.get(name);
    }
    
    public void removeSoundSource(String name)
    {
        this.soundSourceMap.remove(name);
    }
    
    public SoundListener getListener()
    {
        return listener;
    }
    
    public void setListener(SoundListener listener)
    {
        this.listener = listener;
    }
    
    public void addSoundBuffer(SoundBuffer soundBuffer)
    {
        this.soundBufferList.add(soundBuffer);
    }
    
    public void addSoundSource(String name, SoundSource soundSource)
    {
        this.soundSourceMap.put(name, soundSource);
    }
    
    public void cleanup()
    {
        soundSourceMap.values().forEach(SoundSource::cleanup);
        soundSourceMap.clear();
        soundBufferList.forEach(SoundBuffer::cleanup);
        soundBufferList.clear();
        if(context != MemoryUtil.NULL)
        {
            alcDestroyContext(context);
        }
        if(device != MemoryUtil.NULL)
        {
            alcCloseDevice(device);
        }
    }
    
}
