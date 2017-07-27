package ru.mos.polls.wizardprofile.vm;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.wizardprofile.ui.adapter.WizardProfilePagerAdapter;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfileFragmentVM extends FragmentViewModel<WizardProfileFragment, FragmentWizardProfileBinding> {

    ViewPager pager;
    TabLayout tabLayout;

    public WizardProfileFragmentVM(WizardProfileFragment fragment, FragmentWizardProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardProfileBinding binding) {
        pager = binding.pager;
        tabLayout = binding.wizardTabLayout;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        WizardProfilePagerAdapter adapter = new WizardProfilePagerAdapter(getFragment().getChildFragmentManager(), new AgUser(getActivity()));
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager, true);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(0);

            }

            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

//    LinearLayout linearLayout;
//
//    private void drawPageSelectionIndicators(int mPosition){
//        if(linearLayout!=null) {
//            linearLayout.removeAllViews();
//        }
//        linearLayout=(LinearLayout)findViewById(R.id.viewPagerCountDots);
//        dots = new ImageView[dotsCount];
//        for (int i = 0; i < dotsCount; i++) {
//            dots[i] = new ImageView(context);
//            if(i==mPosition)
//                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_selected));
//            else
//                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.item_unselected));
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//
//            params.setMargins(4, 0, 4, 0);
//            linearLayout.addView(dots[i], params);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
