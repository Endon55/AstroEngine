package com.anthonycosenza.editor.external;


import com.anthonycosenza.editor.logger.EditorLogger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Intellij
{
    private final File intellijInstall;
    private final String app;
    private Process process;
    
    
    public Intellij()
    {
        File jetbrainsDirectory = new File(System.getenv("ProgramFiles") + "\\Jetbrains");
        if(!jetbrainsDirectory.exists()) throw new RuntimeException("Couldn't find jetbrains directory");
        intellijInstall = findInstall(jetbrainsDirectory);
        
        if(intellijInstall == null) throw new RuntimeException("Couldn't find an intellij install");
        app = intellijInstall.getAbsolutePath() + "\\bin\\idea64.exe";
    }

    public void open(File projectDirectory)
    {
        if(process == null || !process.isAlive())
        {
            ProcessBuilder builder = new ProcessBuilder(app, projectDirectory.getAbsolutePath());
            try
            {
                process = builder.start();
            } catch(IOException e)
            {
                EditorLogger.error("Failed to open Intellij: " + e);
            }
        }
    }
    
    private File findInstall(File jetbrainsDirectory)
    {
        if(!jetbrainsDirectory.exists()) return null;
        File install = null;
        String[] intellijVersion = null;
        
        for(File jetbrainsProduct : Objects.requireNonNull(jetbrainsDirectory.listFiles()))
        {
            String[] nameSplit = jetbrainsProduct.getName().split(" ");
            String[] productVersion = nameSplit[nameSplit.length - 1].split("\\.");
            if(nameSplit[0].equals("IntelliJ"))
            {
                if(install == null || isMoreRecentVersion(productVersion, intellijVersion))
                {
                    install = jetbrainsProduct;
                    intellijVersion = productVersion;
                }
            }
        }
        return install;
    }
    private boolean isMoreRecentVersion(String[] checkAgainstVersion, String[] baseVersion)
    {
        int length = Math.max(checkAgainstVersion.length, baseVersion.length);
        for(int i = 0; i < length; i++)
        {
            if(i >= baseVersion.length)
            {
                return false;
            }
            else if(i >= checkAgainstVersion.length)
            {
                return true;
            }
            int baseSubVersion = Integer.parseInt(baseVersion[i]);
            int checkSubVersion = Integer.parseInt(checkAgainstVersion[i]);
            if(baseSubVersion > checkSubVersion)
            {
                return false;
            }
            else if(checkSubVersion > baseSubVersion)
            {
                return true;
            }
        }
        return false;
    }
    
    public File getIntellijInstall()
    {
        return intellijInstall;
    }
}
