// IEchoServer.aidl
package github.hotstu.demo.binder;

// Declare any non-default types here with import statements
import github.hotstu.demo.binder.ICallback;


interface IEchoServer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String sendSignal(String str);

    void registerCallback(ICallback callback);
    void unRegisterCallback(ICallback callback);
}
