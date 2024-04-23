package com.anthonycosenza.events;

import com.anthonycosenza.input.Key;
import com.anthonycosenza.input.KeyAction;

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
