package com.anthonycosenza.engine.space.node;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/*
 * This class is used to ignore values that don't need to be serialized.
 * The Runtime annotation keeps doesn't discard this flag when compiling, saying that code somewhere else relies on this at runtime.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore
{
}
