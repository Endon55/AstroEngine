package com.anthonycosenza.engine.space.node;


import com.anthonycosenza.engine.util.math.EngineMath;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    private long resourceID;
    private String name;
    
    private transient Node parent;
    private transient boolean initialized = false;
    private final List<Node> children;

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
    
    public void setParent(Node parent)
    {
        this.parent = parent;
    }
    
    public Node getParent()
    {
        return this.parent;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public List<Node> getChildren()
    {
        return this.children;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    protected long createNodeID()
    {
        return EngineMath.generateMaxLengthLong();
    }
    
    public void addChild(Node child)
    {
        child.parent = this;
        this.children.add(child);
    }
    public void removeChild(Node child)
    {
        this.children.remove(child);
    }
    public void initialize()
    {
        
        initialized = true;
    }
    
    public void update(float delta)
    {
        if(!initialized) initialize();
        for(Node child : this.children)
        {
            child.update(delta);
        }
    }
    
    public void updatePhysics(float delta)
    {
        if(!initialized) initialize();
        for(Node child : this.children)
        {
            child.updatePhysics(delta);
        }
    }
    
    public void updateUI(float delta)
    {
        if(!initialized) initialize();
        for(Node child : this.children)
        {
            child.updateUI(delta);
        }
    }
    public void setResourceID(long resourceID)
    {
        this.resourceID = resourceID;
    }
    
    public long getResourceID()
    {
        return this.resourceID;
    }
    
    @Override
    public String toString()
    {
        return "Node{" +
                "name='" + this.name + '\'' +
                ", children=" + this.children +
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
