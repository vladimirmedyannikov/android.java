package ru.mos.polls.wizardprofile.vm;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.ui.fragment.NewFlatFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;
import ru.mos.polls.profile.gui.fragment.BindingSocialFragment;
import ru.mos.polls.wizardprofile.ui.adapter.WizardProfilePagerAdapter;
import ru.mos.polls.wizardprofile.ui.fragment.MakeAvatarFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfileFragmentVM extends FragmentViewModel<WizardProfileFragment, FragmentWizardProfileBinding> {

    ViewPager pager;
    TabLayout tabLayout;
    AgUser agUser;
    Button nextButton;
    List<Fragment> list;

    WizardProfilePagerAdapter adapter;

    public WizardProfileFragmentVM(WizardProfileFragment fragment, FragmentWizardProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardProfileBinding binding) {
        pager = binding.pager;
        tabLayout = binding.wizardTabLayout;
        agUser = new AgUser(getActivity());
        nextButton = binding.wizardAction;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        nextButton.setOnClickListener(v -> {
            doNext();
        });
        list = new ArrayList<>();
        list.add(new MakeAvatarFragment());
        list.add(EditPersonalInfoFragment.newInstance(agUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL));
        list.add(EditPersonalInfoFragment.newInstance(agUser, EditPersonalInfoFragmentVM.PERSONAL_FIO));
        list.add(EditPersonalInfoFragment.newInstance(agUser, EditPersonalInfoFragmentVM.COUNT_KIDS));
        list.add(EditPersonalInfoFragment.newInstance(agUser, EditPersonalInfoFragmentVM.BIRTHDAY_KIDS));
        list.add(NewFlatFragment.newInstance(agUser.getRegistration(), NewFlatFragmentVM.FLAT_TYPE_REGISTRATION));
        list.add(NewFlatFragment.newInstance(agUser.getResidence(), NewFlatFragmentVM.FLAT_TYPE_RESIDENCE));
        list.add(NewFlatFragment.newInstance(agUser.getWork(), NewFlatFragmentVM.FLAT_TYPE_WORK));
        list.add(BindingSocialFragment.newInstance(true));
        adapter = new WizardProfilePagerAdapter(getFragment().getChildFragmentManager(), agUser, list);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager, true);

        setDotCustomView();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

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
                try {
                    ImageView dot = (ImageView) tab.getCustomView().findViewById(R.id.wizard_dot);
//                Drawable myDrawable = getFragment().getResources().getDrawable(R.drawable.wizard_profile_warning_dot);
//                dot.setImageDrawable(myDrawable);
                    dot.setActivated(false);
                    dot.setEnabled(false);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void setDotCustomView() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTabView());
        }
    }

    public void removeFragment(int index) {
        list.remove(index);
        adapter.notifyDataSetChanged();
        setDotCustomView();
    }

    public void addFragment(int index, Fragment fragment) {
        list.add(index, fragment);
        adapter.notifyDataSetChanged();
        setDotCustomView();
    }

    private void doNext() {
    }

    public View getTabView() {
        return LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.item_wizard_profile_dot, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
