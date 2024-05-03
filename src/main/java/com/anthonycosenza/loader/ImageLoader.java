package com.anthonycosenza.loader;



public class ImageLoader
{
    private ImageLoader(String filepath)
    {

    }
    public static int[] load(String filepath)
    {
        if(fileFormat(filepath).equals("png"))
        {
            PNGLoader loader = new PNGLoader(filepath);
            return loader.pixelData;
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

