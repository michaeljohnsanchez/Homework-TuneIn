package x.homework.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import x.homework.R;
import x.homework.network.FooApi;
import x.homework.presenters.DirectoryPresenter;

/**
 * @author michael sanchez
 */
public class DirectoryActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private TextView pageTitle;
    private ProgressBar progressBar;
    private ViewImpl viewImpl;

    public View getView() {
        return viewImpl;
    }

    // ok for the coding exercise but I wouldn't allow this dependency in 'real life'
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_activity);
        fragmentManager = getSupportFragmentManager();
        viewImpl = new ViewImpl(this);
        DirectoryPresenter presenter = new DirectoryPresenter(new ViewImpl(this));
        // match status bar color with toolbar
        // again - just for this exercise
        //noinspection deprecation
        getWindow().setStatusBarColor(getResources().getColor(R.color.teal));
        renderToolbar();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        presenter.showPage(FooApi.BASE_URL);
    }

    private void renderToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                onBackPressed();
            }
        });
        pageTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
    }

    public interface View {

        void setLoading(boolean loading);

        void setPageTitle(String title);

        void startFragmentForPage(Fragment fragment);
    }


    private static class ViewImpl implements View {

        private final WeakReference<DirectoryActivity> actRef;
        private DirectoryActivity act;

        private ViewImpl(DirectoryActivity activity) {
            actRef = new WeakReference<>(activity);
        }

        private void addFragment(int containerViewId, Fragment fragment) {
            if (!getAndSet()) return;
            act.fragmentManager.beginTransaction()
                    .replace(containerViewId, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        private boolean getAndSet() {
            act = actRef.get();
            return act != null;
        }

        @Override
        public void setLoading(boolean loading) {
            if (!getAndSet()) return;
            int visibility = loading ? android.view.View.VISIBLE : android.view.View.GONE;
            act.progressBar.setVisibility(visibility);
        }

        @Override
        public void setPageTitle(String title) {
            if (!getAndSet()) return;
            act.pageTitle.setText(title);
        }

        @Override
        public void startFragmentForPage(Fragment fragment) {
            if (!getAndSet()) return;
            addFragment(R.id.fragment_container, fragment);
        }
    }
}