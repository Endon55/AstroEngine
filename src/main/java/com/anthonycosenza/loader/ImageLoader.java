package com.anthonycosenza.loader;


import java.util.Arrays;

public class ImageLoader
{
    private ImageLoader() { }
    
    public static float[] load(int[] dimensions, String filepath)
    {
        if(fileFormat(filepath).equals("png"))
        {
            PNGLoader loader = new PNGLoader(filepath, true);
            dimensions[0] = loader.width;
            dimensions[1] = loader.height;
            float[] values = new float[loader.pixelData.length];
            for(int i = 0; i < loader.pixelData.length; i++)
            {
                values[i] = (loader.pixelData[i] & 0xff) / 255f;
            }
            return values;
        }
        else throw new RuntimeException("Can't handle files of type : " + fileFormat(filepath));
    }
    
    private static String fileFormat(String filepath)
    {
        String[] split = filepath.split("\\.");
        if(split.length < 2) throw new RuntimeException("Invalid image format for path: " + filepath);
        return split[split.length -1];
    }
}

