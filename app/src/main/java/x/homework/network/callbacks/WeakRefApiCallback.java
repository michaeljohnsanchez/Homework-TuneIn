package x.homework.network.callbacks;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author michael sanchez
 */
public abstract class WeakRefApiCallback<Caller, T> implements Callback<T> {

    private WeakReference<Caller> callerRef;

    public WeakRefApiCallback(Caller caller) {
        callerRef = new WeakReference<>(caller);
    }

    protected abstract void onFailureCalled(Caller caller, Call<T> call, Throwable t);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Caller caller = callerRef.get();
        if (caller != null) {
            onResponseCalled(caller, call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Caller caller = callerRef.get();
        if (caller != null) {
            onFailureCalled(caller, call, t);
        }
    }

    protected abstract void onResponseCalled(Caller caller, Call<T> call, Response<T> response);
}