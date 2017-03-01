package x.homework.presenters;

import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import x.homework.fragments.PageFragment;
import x.homework.model.ApiResponse;
import x.homework.model.Item;
import x.homework.network.FooApi;
import x.homework.network.callbacks.WeakRefApiCallback;
import x.homework.network.utils.RetrofitUtils;

/**
 * @author michael sanchez
 */
public class PagePresenter implements Presenter {

    private final PageFragment.View view;

    public PagePresenter(PageFragment.View view) {
        this.view = view;
    }

    private void displayToast(String localizedMessage, int duration) {
        view.displayToast(localizedMessage, duration);
    }

    // the url provided once attached is used for fetching the current page 
    public void onAttached() {
        FooApi api = RetrofitUtils.getRetrofit().create(FooApi.class);
        // when main activity is loaded, populate with top-level directory items
        String url = view.getUrl();
        if (StringUtils.isNotBlank(url)) {
            api.get(url).enqueue(new PageCallback(this));
            showProgressBar(true);
        }
    }

    private void setAdapterItems(List<Item> items) {
        view.appendData(items);
    }

    private void setPageTitle(String title) {
        view.setPageTitle(title);
    }

    public void showPage(String url) {
        view.startFragmentForPage(PageFragment.getFragment(url));
    }

    private void showProgressBar(boolean loadingState) {
        view.setLoading(loadingState);
    }

    private static class PageCallback extends WeakRefApiCallback<PagePresenter, ApiResponse> {

        private PageCallback(PagePresenter presenter) {
            super(presenter);
        }

        @Override
        protected void onFailureCalled(PagePresenter presenter, Call<ApiResponse> call, Throwable t) {
            // in a live impl we'd probably need to take other action (Crashlytics, Fabric, etc)
            // and perhaps make this a default override-able impl in the parent
            presenter.showProgressBar(false);
            presenter.displayToast(t.getLocalizedMessage(), Toast.LENGTH_LONG);
        }

        @Override
        protected void onResponseCalled(PagePresenter presenter, Call<ApiResponse> call, Response<ApiResponse> response) {
            presenter.showProgressBar(false);
            ApiResponse result = response.body();
            presenter.setPageTitle(result.getHead().getTitle());
            presenter.setAdapterItems(result.getItems());
        }
    }
}