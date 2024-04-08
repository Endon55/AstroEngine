package com.anthonycosenza.engine.sound;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_info;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_samples_short_interleaved;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_open_filename;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_stream_length_in_samples;

public class SoundBuffer
{
    private final int bufferId;
    private ShortBuffer pcm;
    
    public SoundBuffer(String filePath)
    {
        this.bufferId = alGenBuffers();
        try(STBVorbisInfo info = STBVorbisInfo.malloc())
        {
            pcm = readVorbis(filePath, info);
            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
            
        }
    }
    
    private ShortBuffer readVorbis(String filePath, STBVorbisInfo info)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_filename(filePath, error, null);
            if(decoder == MemoryUtil.NULL)
            {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }
            
            stb_vorbis_get_info(decoder, info);
            
            int channels = info.channels();
            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
            
            ShortBuffer result = MemoryUtil.memAllocShort(lengthSamples * channels);
            
            result.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, result) * channels);
            stb_vorbis_close(decoder);
            
            return result;
        }
    }
    
    public int getBufferId()
    {
        return bufferId;
    }
    
    public void cleanup()
    {
        alDeleteBuffers(this.bufferId);
        if(pcm != null)
        {
            MemoryUtil.memFree(pcm);
        }
    }
}
