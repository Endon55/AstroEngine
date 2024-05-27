package com.anthonycosenza.engine.space.node;


import java.util.ArrayList;
import java.util.List;

public class Node
{
    public String name;
    @Ignore
    public Node parent;
    public List<Node> children;
    public Node()
    {
        this.name = "";
        this.children = new ArrayList<>();
        
    }
    public Node(String name, List<Node> children)
    {
        this.name = name;
        if(children != null)
        {
            this.children = new ArrayList<>(children);
        }
        else this.children = new ArrayList<>();
    }
    
    @Override
    public String toString()
    {
        return "Node{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
