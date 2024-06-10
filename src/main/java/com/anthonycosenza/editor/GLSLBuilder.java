package com.anthonycosenza.editor;

import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;
import com.anthonycosenza.engine.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GLSLBuilder
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
    boolean vertex;
    
    public GLSLBuilder(File file)
    {
        String extension = FileUtils.getExtension(file);
        if(extension.endsWith("vert"))
        {
            vertex = true;
        }
        else if(extension.endsWith("frag"))
        {
            vertex = false;
        }
        else throw new RuntimeException("Don't know this file type: " + extension);
        
        
        fileContentsRaw = FileUtils.getFileContents(file.getPath());
        fileContents = clean(fileContentsRaw.split("\\r?\\n"));
        
        parse();
    }
    private GLSLBuilder(String shaderCode, boolean isVertex)
    {
        vertex = isVertex;
        fileContentsRaw = shaderCode;
        fileContents = clean(fileContentsRaw.split("\\r?\\n"));
        
        parse();
    }
    
    
    private List<String> buildVertex()
    {
        GLSLBuilder baseVertex = new GLSLBuilder(VertexShader.DEFAULT.getShaderCode(), true);
        List<String> lines = new ArrayList<>();
        lines.addAll(this.layouts);
        lines.addAll(baseVertex.layouts);
        
        lines.addAll(this.structs);
        lines.addAll(baseVertex.structs);
        
        lines.addAll(this.uniforms);
        lines.addAll(baseVertex.uniforms);
        
        lines.addAll(this.inout);
        lines.addAll(baseVertex.inout);
    
        String mainLine = null;
        for(int i = 0; i < functions.size(); i++)
        {
            String name = functionNames.get(i);
            if(name.equals("modelToWorld"))
            {
                if(mainLine != null) throw new RuntimeException("Multiple shader functions in one file");
                
                mainLine = "gl_Position = projectionMatrix * cameraMatrix * modelToWorld(vec4(inPosition, 1.0));";
            }
            lines.add(functions.get(i));
        }
    
        if(mainLine == null) throw new RuntimeException("No primary function in vertex shader");
        lines.add("void main() \n{\n" + mainLine + "\n}\n");
        
        
        return lines;
    }
    
    private List<String> buildFragment()
    {
        GLSLBuilder baseFragment = new GLSLBuilder(FragmentShader.DEFAULT.getShaderCode(), false);
        List<String> lines = new ArrayList<>();
        lines.addAll(this.layouts);
        lines.addAll(baseFragment.layouts);
    
        lines.addAll(this.structs);
        lines.addAll(baseFragment.structs);
    
        lines.addAll(this.uniforms);
        lines.addAll(baseFragment.uniforms);
        
        lines.addAll(this.inout);
        lines.addAll(baseFragment.inout);
    
        String mainLine = null;
        for(int i = 0; i < functions.size(); i++)
        {
            String name = functionNames.get(i);
            if(name.equals("fragmentColor"))
            {
                if(mainLine != null) throw new RuntimeException("Multiple shader functions in one file");
            
                mainLine = "fragColor = fragmentColor();";
            }
            lines.add(functions.get(i));
        }
        if(mainLine == null) throw new RuntimeException("No primary function in fragment shader");
        lines.add("void main() \n{\n" + mainLine + "\n}\n");
    
    
        return lines;
    }
    
    public String build()
    {
        List<String> lines;
        if(vertex)
        {
            lines = buildVertex();
        }
        else lines = buildFragment();
        
        StringBuilder builder = new StringBuilder();
        builder.append(this.version).append("\n");
        lines.forEach(string -> builder.append(string).append("\n"));
    
        return builder.toString();
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
                String name = splitSignature[1].split("\\(")[0];
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
            else if(line.startsWith("in ") || line.startsWith("out "))
            {
                inout.add(line);
            }
        }
    }
    public static String mergeGLSL(File baseShader, File additiveShader)
    {
        GLSLBuilder base = new GLSLBuilder(baseShader);
        GLSLBuilder add = new GLSLBuilder(additiveShader);
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
