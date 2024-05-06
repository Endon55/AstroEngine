package com.anthonycosenza.engine.events;

import com.anthonycosenza.engine.input.Key;
import com.anthonycosenza.engine.input.KeyAction;

public class KeyEvent
{
    public final Key key;
    public final KeyAction keyAction;
    
    public KeyEvent(Key key, KeyAction keyAction)
    {
        this.key = key;
        this.keyAction = keyAction;
    }
}
