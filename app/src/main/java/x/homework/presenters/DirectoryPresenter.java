package x.homework.presenters;

import x.homework.activities.DirectoryActivity;
import x.homework.fragments.PageFragment;

/**
 * @author michael sanchez
 */
public class DirectoryPresenter implements Presenter {

    private final DirectoryActivity.View view;

    public DirectoryPresenter(DirectoryActivity.View view) {
        this.view = view;
    }

    public void showPage(String url) {
        view.startFragmentForPage(PageFragment.getFragment(url));
    }
}