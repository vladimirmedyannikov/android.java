package ru.mos.polls.tutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.R;
import ru.mos.polls.SplashActivity;
import ru.mos.polls.util.GuiUtils;

public class TutorialFragment extends Fragment {
    public static final String ARG_TUTORIAL = "arg_tutorial";

    public static TutorialFragment newInstance(Tutorial[] tutorialItems) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TUTORIAL, tutorialItems);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.tutorial_dots_container)
    LinearLayout dotsContainer;
    @BindView(R.id.tutorial_slider)
    ViewPager viewPager;
    @BindView(R.id.tutorial_action)
    Button action;

    private Tutorial[] content;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        GuiUtils.setStatusBarColor(getActivity(), R.color.tutorial_background);

        if (getArguments() != null) {
            content = (Tutorial[]) getArguments().getSerializable(ARG_TUTORIAL);
        }

        final ArrayList<ImageView> dots = new ArrayList<ImageView>();
        for (Tutorial tutorial : content) {
            ImageView imageView = (ImageView) View.inflate(getContext(), R.layout.tutorial_dot, null);
            dots.add(imageView);
            imageView.setEnabled(false);
            dotsContainer.addView(imageView);
        }

        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                action.setText(position == viewPager.getAdapter().getCount() - 1 ?
                        R.string.tutorial_cancel : R.string.tutorial_skip);
            }

            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
                dots.get(position).setEnabled(true);
                if (position != 0){
                    dots.get(position - 1).setEnabled(false);
                }
                if (position != viewPager.getAdapter().getCount() - 1){
                    dots.get(position + 1).setEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick(R.id.tutorial_action)
    public void onAction() {
        Manager.setShow(getContext());
        SplashActivity.startApp(getContext());
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialPageFragment.newInstance(content[position]);
        }

        @Override
        public int getCount() {
            return content.length;
        }

    }

    public static class Manager {
        private static final String PREFS = "tutorial_prefs";
        private static final String WAS_SHOW = "was_show";

        public static boolean wasShow(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return prefs.getBoolean(WAS_SHOW, false);
        }

        public static void setShow(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(WAS_SHOW, true).apply();
        }
    }
}
