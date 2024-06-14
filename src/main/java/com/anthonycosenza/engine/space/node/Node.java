package com.anthonycosenza.engine.space.node;


import com.anthonycosenza.engine.util.math.EngineMath;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    private long resourceID;
    private String name;
    
    protected transient Node bound;
    private transient Node parent;
    private List<Node> children;
    public Node(Node bindNode)
    {
        bind(bindNode);
    }
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
        bound = this;
        this.name = name;
        if(children != null)
        {
            this.children = new ArrayList<>(children);
        }
        else this.children = new ArrayList<>();
        
        this.resourceID = createNodeID();
    }
    public void bind(Node node)
    {
        bound = node;
    }
    public void unbind()
    {
        bound = this;
    }
    public void setParent(Node parent)
    {
        bound.parent = parent;
    }
    
    public Node getParent()
    {
        return bound.parent;
    }
    
    public String getName()
    {
        return bound.name;
    }
    
    public List<Node> getChildren()
    {
        return bound.children;
    }
    
    public void setName(String name)
    {
        bound.name = name;
    }
    
    protected long createNodeID()
    {
        return EngineMath.generateMaxLengthLong();
    }
    
    public void addChild(Node child)
    {
        child.parent = bound;
        bound.children.add(child);
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
        for(Node child : bound.children)
        {
            child.update(delta);
            child.updateChildren(delta);
        }
    }
    
    public void updateChildrenPhysics(float delta)
    {
        for(Node child : bound.children)
        {
            child.updatePhysics(delta);
            child.updateChildrenPhysics(delta);
        }
    }
    
    public void updateChildrenUI(float delta)
    {
        for(Node child : bound.children)
        {
            child.updateUI(delta);
            child.updateChildrenUI(delta);
        }
    }
    
    public void setResourceID(long resourceID)
    {
        bound.resourceID = resourceID;
    }
    
    public long getResourceID()
    {
        return bound.resourceID;
    }
    
    @Override
    public String toString()
    {
        return "Node{" +
                "name='" + bound.name + '\'' +
                ", children=" + bound.children +
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
