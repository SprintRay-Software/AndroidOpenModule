// IRocListener.aidl
package com.sprintray.roc;

// Declare any non-default types here with import statements

interface IRocListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   void onCallback(in String message);
}