package com.anthonycosenza.editor.scene;

import com.anthonycosenza.Project;
import com.anthonycosenza.engine.space.runtime.Entity;
import com.anthonycosenza.engine.space.runtime.Scene;

import java.util.ArrayList;
import java.util.List;

public class EditorScene implements Scene
{
    private Project source;
    private List<Entity> entities;
    
    public EditorScene(Project source)
    {
        entities = new ArrayList<>();
        //load scene
    }
    @Override
    public Entity getEntity(int index)
    {
        Entity entity = entities.get(index);
        /*if(source.isModified(entity));
        {
            entity = source.loadEntity(index);
        }*/
        //check if entity was modified before returning.
        return entity;
    }
}
