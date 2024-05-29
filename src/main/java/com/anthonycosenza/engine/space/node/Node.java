package com.anthonycosenza.engine.space.node;


import com.anthonycosenza.engine.annotations.Ignore;
import com.anthonycosenza.engine.util.math.EngineMath;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    @Ignore
    private final long id;
    public String name;
    @Ignore
    public Node parent;
    public List<Node> children;
    public Node()
    {
        this.name = "";
        this.children = new ArrayList<>();
        id = EngineMath.generateMaxLengthLong();
    }
    public Node(String name, List<Node> children)
    {
        this.name = name;
        if(children != null)
        {
            this.children = new ArrayList<>(children);
        }
        else this.children = new ArrayList<>();
        
        id = EngineMath.generateMaxLengthLong();
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
        return id;
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
    
        return id == node.id;
    }
    
    @Override
    public int hashCode()
    {
        return (int) (id ^ (id >>> 32));
    }
}
