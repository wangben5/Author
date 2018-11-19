package websdk;

/**
 * Created by cuiboqiang on 08/06/2018.
 */

public interface NativeInterace {
    void onWxLoin(Object paramsValue, CompletionHandler<Object> handler);
    void onWxPay(Object paramsValue, CompletionHandler<Object> handler);
    void onGetUserInfo(Object paramsValue, CompletionHandler<Object> handler);
}
