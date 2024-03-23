package com.anthonycosenza.engine.render;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TextureCache
{
    public static final String DEFAULT_TEXTURE = System.getProperty("user.dir") + "/resources/models/default/DefaultTexture.png";
    
    private Map<String, Texture> textureMap;
    
    public TextureCache()
    {
        textureMap = new HashMap<>();
        textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
    }
    
    public Texture createTexture(String texturePath)
    {
        return textureMap.computeIfAbsent(texturePath, Texture::new);
    }
    public Texture getTexture(String texturePath)
    {
        return textureMap.get(texturePath);
    }
    
    
    public void cleanup()
    {
        textureMap.values().forEach(Texture::cleanup);
    }
    
}
