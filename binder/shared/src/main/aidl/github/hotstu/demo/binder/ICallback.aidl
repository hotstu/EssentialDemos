// ICallback.aidl
package github.hotstu.demo.binder;

// Declare any non-default types here with import statements

interface ICallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onEvent(String aString);
}
