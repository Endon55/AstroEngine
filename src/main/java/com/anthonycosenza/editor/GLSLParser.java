package com.anthonycosenza.editor;

import com.anthonycosenza.engine.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GLSLParser
{
    //Read GLSL file
    //Extract structs, global vars, layouts, and uniforms(to be merged into the source file).
    //Determine which functions have been used and delete the rest.
    //Insert the function call into main function.
    
    String fileContentsRaw;
    List<String> fileContents;
    
    public String version = "";
    public final List<String> layouts = new ArrayList<>();
    public final List<String> structs = new ArrayList<>();
    public final List<String> uniforms = new ArrayList<>();
    public final List<String> functions = new ArrayList<>();
    public final List<String> functionNames = new ArrayList<>();
    public final List<String> functionTypes = new ArrayList<>();
    public final List<String> inout = new ArrayList<>();
    
    
    public GLSLParser(File file)
    {
        fileContentsRaw = FileUtils.getFileContents(file.getPath());
        fileContents = clean(fileContentsRaw.split("\\r?\\n"));
    }
    public GLSLParser(String filepath)
    {
        fileContentsRaw = FileUtils.getFileContents(filepath);
        fileContents = clean(fileContentsRaw.split("\\r?\\n"));
    }
    

    private List<String> clean(String[] fileContents)
    {
        return Arrays.stream(fileContents).filter(string -> !string.isBlank()).map(String::strip).collect(Collectors.toList());
    }
    
    public void parse()
    {
        stripOneLiners();
        stripStructs();
        stripFunctions();
    }
    
    private void stripFunctions()
    {
        boolean inside = false;
        StringBuilder function = new StringBuilder();
        for(int i = 0; i < fileContents.size(); i++)
        {
            String line = fileContents.get(i);
            if(inside && (line.startsWith("}") || line.endsWith("}")))
            {
                function.append(line);
                inside = false;
                String[] funcSignature = function.toString().split("\\{");
                String[] splitSignature = funcSignature[0].split(" ");
                String type = splitSignature[0];
                String name = splitSignature[1].substring(0, splitSignature[1].length() - 2);
                String funcBody = funcSignature[1].split("\\}")[0];
                if(!funcBody.isBlank())
                {
                    funcBody = function.toString().replace("; ", ";\n").replace("{ ", "{\n");
                    functions.add(funcBody);
                    functionNames.add(name);
                    functionTypes.add(type);
                }
            }
            else if(inside || line.endsWith(")"))
            {
                if(!line.startsWith("//"))
                {
                    function.append(line).append(" ");
                }
                inside = true;
            }
        }
    }
    
    private void stripStructs()
    {
        boolean inside = false;
        StringBuilder struct = new StringBuilder();
        for(int i = 0; i < fileContents.size(); i++)
        {
            String line = fileContents.get(i);
            if(inside && (line.startsWith("};") || line.endsWith("};")))
            {
                struct.append(line).append(System.getProperty("line.separator"));
                inside = false;
                structs.add(struct.toString());
            }
            else if(inside || line.startsWith("struct"))
            {
                struct.append(line).append(System.getProperty("line.separator"));
                inside = true;
            }
        }
    }
    
    private void stripOneLiners()
    {
        for(int i = 0; i < fileContents.size(); i++)
        {
            String line = fileContents.get(i).strip();
            if(line.startsWith("//"))
            {
                continue;
            }
            else if(line.startsWith("#version"))
            {
                version = line;
            }
            else if(line.startsWith("layout"))
            {
                layouts.add(line);
            }
            else if(line.startsWith("uniform"))
            {
                uniforms.add(line);
            }
            else if(line.startsWith("in") || line.startsWith("out"))
            {
                inout.add(line);
            }
        }
    }
    public static String mergeGLSL(File baseShader, File additiveShader)
    {
        GLSLParser base = new GLSLParser(baseShader);
        GLSLParser add = new GLSLParser(additiveShader);
        base.parse();
        add.parse();
    
        List<String> layouts = new ArrayList<>(add.layouts);
        List<String> structs = new ArrayList<>(add.structs);
        List<String> uniforms = new ArrayList<>(add.uniforms);
        List<String> inout = new ArrayList<>(add.inout);
        List<String> functions = new ArrayList<>();
    
        layouts.addAll(base.layouts);
        structs.addAll(base.structs);
        uniforms.addAll(base.uniforms);
        inout.addAll(base.inout);
    
        StringBuilder builder = new StringBuilder();
        
        builder.append(base.version).append("\n");
        layouts.forEach(string -> builder.append(string).append("\n"));
        uniforms.forEach(string -> builder.append(string).append("\n"));
        inout.forEach(string -> builder.append(string).append("\n"));
        structs.forEach(string -> builder.append(string).append("\n"));
    
    
        List<String> duplicates = new ArrayList<>();
        for(int i = 0; i < base.functions.size(); i++)
        {
            String function = base.functions.get(i);
            String name = base.functionNames.get(i);
            String type = base.functionTypes.get(i);
            if(add.functionNames.contains(name))
            {
                duplicates.add(name);
            }
            
            functions.add(function);
        }
        for(int i = 0; i < add.functions.size(); i++)
        {
            String function = add.functions.get(i);
            if(!duplicates.contains(add.functionNames.get(i)))
            {
                functions.add(function);
            }
        }
    
        functions.forEach(string -> builder.append(string).append("\n"));
        
        return builder.toString();
    }
    
}
