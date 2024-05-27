package com.anthonycosenza;


import com.anthonycosenza.editor.scene.NodeDeSerializer;
import com.anthonycosenza.editor.scene.NodeSerializer;
import com.anthonycosenza.engine.space.node.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main
{

    public static void main(String[] args) throws InterruptedException, IOException
    {
        //Editor editor = new Editor();
        File file = new File("C:\\Users\\antho\\OneDrive\\Desktop\\Test\\project.astro");
        if(!file.exists())
        {
            throw new RuntimeException("File doesn't exist");
        }
        
        Node node = new Node("Anthony", List.of(
                new Node("Sasha", List.of(new Node("Rabbit", null))),
                new Node("Kevin", List.of(new Node("Karin", null), new Node("Kristopher", null)))));
    
    
        NodeSerializer.serialize(file, node);
        System.out.println(NodeDeSerializer.deSerialize(file));
    }
}