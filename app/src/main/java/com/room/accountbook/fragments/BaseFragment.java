package com.room.accountbook.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.room.accountbook.R;


/**
 * Created by subash on 13/8/17.
 */

public class BaseFragment extends Fragment {

    protected SetFloatingListener listener;
    protected FragmentActivity activity;

    protected void setTitle(String name) {
        if (activity != null && activity instanceof TitleChangeListerner) {
            ((TitleChangeListerner) activity).onTitleChanged(name);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetFloatingListener) {
            listener = (SetFloatingListener) context;
        }
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SetFloatingListener) {
            listener = (SetFloatingListener) activity;
        }

        if (activity instanceof FragmentActivity) {
            this.activity = (FragmentActivity) activity;
        }
    }

    protected void navigateTo(BaseFragment fragment, String tag, boolean addToBackstack, boolean addAnimation, String clearUpto) {
        if (activity == null) return;

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        FragmentManager manager = activity.getSupportFragmentManager();
        if (TextUtils.isEmpty(clearUpto))
            manager.popBackStack(clearUpto, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = manager.beginTransaction();

        if (addAnimation)
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        if (transaction != null) {
            transaction.replace(R.id.container, fragment, tag);

            if (addToBackstack)
                transaction.addToBackStack(tag);

            transaction.commit();
        }
    }

    protected void navigateTo(BaseFragment fragment, String tag, boolean addToBackstack, boolean addAnimation) {
        navigateTo(fragment, tag, addToBackstack, addAnimation, null);
    }

    protected void navigateTo(BaseFragment fragment, String tag, boolean addToBackstack) {
        navigateTo(fragment, tag, addToBackstack, true);
    }

    protected void removeBackStack() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    public interface SetFloatingListener {
        public void setFloatingListener(View.OnClickListener listener);
    }

    protected void navigateScreen(Fragment fragment, String tag) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    public interface TitleChangeListerner {
        public void onTitleChanged(String title);
    }
}
