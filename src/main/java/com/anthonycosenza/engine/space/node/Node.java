package com.anthonycosenza.engine.space.node;


import com.anthonycosenza.engine.util.math.EngineMath;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    public long resourceID;
    public String name;
    
    public transient Node parent;
    public List<Node> children;
    
    public Node(List<Node> children)
    {
        this("", children);
    }
    public Node(String name)
    {
        this(name, new ArrayList<>());
    }
    public Node()
    {
        this("", new ArrayList<>());
    }
    public Node(String name, List<Node> children)
    {
        this.name = name;
        if(children != null)
        {
            this.children = new ArrayList<>(children);
        }
        else this.children = new ArrayList<>();
        
        this.resourceID = createNodeID();
    }
    
    protected long createNodeID()
    {
        return EngineMath.generateMaxLengthLong();
    }
    
    public void addChild(Node child)
    {
        child.parent = this;
        children.add(child);
    }
    public void initialize()
    {
    
    }
    
    public void update(float delta)
    {
    
    }
    
    public void updatePhysics(float delta)
    {
    
    }
    
    public void updateUI(float delta)
    {
    
    }
    
    public void updateChildren(float delta)
    {
        for(Node child : children)
        {
            child.update(delta);
            child.updateChildren(delta);
        }
    }
    
    public void updateChildrenPhysics(float delta)
    {
        for(Node child : children)
        {
            child.updatePhysics(delta);
            child.updateChildrenPhysics(delta);
        }
    }
    
    public void updateChildrenUI(float delta)
    {
        for(Node child : children)
        {
            child.updateUI(delta);
            child.updateChildrenUI(delta);
        }
    }
    
    public long getId()
    {
        return resourceID;
    }
    
    @Override
    public String toString()
    {
        return "Node{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Node node = (Node) o;
    
        return resourceID == node.resourceID;
    }
    
    @Override
    public int hashCode()
    {
        return (int) (resourceID ^ (resourceID >>> 32));
    }
}
