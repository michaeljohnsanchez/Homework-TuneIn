package x.homework.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import x.homework.R;
import x.homework.activities.DirectoryActivity;
import x.homework.adapters.ItemAdapter;
import x.homework.model.Item;
import x.homework.presenters.PagePresenter;

/**
 * @author michael sanchez
 */
public class PageFragment extends Fragment {

    private static final String URL = "URL";
    private DirectoryActivity.View activityView;
    private ItemAdapter adapter;
    private PagePresenter presenter;
    private String url;

    public static Fragment getFragment(String url) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(URL, url);
        Fragment fragment = new PageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initializeRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //noinspection ConstantConditions
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.page_fragment_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.bringToFront();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity host = getActivity();
        if (host != null) {
            //noinspection ConstantConditions
            activityView = ((DirectoryActivity) host).getView();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new PagePresenter(new ViewImpl(this));
        adapter = new ItemAdapter(presenter);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = (String) bundle.getSerializable(URL);
        }
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.page_fragment, container, false);
        initializeRecyclerView();
        renderToolbar();
        presenter.onAttached();
        return null;
    }

    private void renderToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
    }

    public interface View {

        void appendData(List<Item> items);

        void displayToast(String message, int duration);

        String getUrl();

        void setLoading(boolean loading);

        void setPageTitle(String title);

        void startFragmentForPage(Fragment fragment);
    }

    private static class ViewImpl implements View {

        private final WeakReference<PageFragment> fragmentRef;
        private PageFragment fragment;

        private ViewImpl(PageFragment fragment) {
            this.fragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void appendData(List<Item> items) {
            if (!doDeRef()) return;
            fragment.adapter.setItems(items);
        }

        @Override
        public void displayToast(String message, int duration) {
            if (!doDeRef()) return;
            Toast.makeText(fragment.getActivity(), message, duration).show();
        }

        @Override
        @Nullable
        public String getUrl() {
            if (!doDeRef()) return null;
            return fragment.url;
        }

        @Override
        public void setLoading(boolean loading) {
            if (!doDeRef()) return;
            // delegates to activity
            if (fragment.activityView != null) {
                fragment.activityView.setLoading(loading);
            }
        }

        @Override
        public void setPageTitle(String title) {
            if (!doDeRef()) return;
            // delegates to activity
            if (fragment.activityView != null) {
                fragment.activityView.setPageTitle(title);
            }
        }

        @Override
        public void startFragmentForPage(Fragment f) {
            if (!doDeRef()) return;
            // delegates to activity
            if (fragment.activityView != null) {
                fragment.activityView.startFragmentForPage(f);
            }
        }

        // I just wanted to right a method called "doodee()"
        // Next up: "poopee()"
        private boolean doDeRef() {
            fragment = fragmentRef.get();
            return fragment != null;
        }
    }
}