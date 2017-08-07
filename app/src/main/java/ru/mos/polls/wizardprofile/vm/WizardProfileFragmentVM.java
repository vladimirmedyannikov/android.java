package ru.mos.polls.wizardprofile.vm;


import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.ui.NavigateFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.ui.fragment.PguAuthFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;
import ru.mos.polls.profile.gui.fragment.BindingSocialFragment;
import ru.mos.polls.wizardprofile.ui.adapter.WizardProfilePagerAdapter;
import ru.mos.polls.wizardprofile.ui.fragment.MakeAvatarFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFamilyFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFlatFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardPersonalDataFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfileFragmentVM extends FragmentViewModel<WizardProfileFragment, FragmentWizardProfileBinding> {

    ViewPager pager;
    TabLayout tabLayout;
    AgUser agUser;
    Button nextButton;
    ArrayMap<String, Fragment> list;

    WizardProfilePagerAdapter adapter;
    ArrayMap<String, Boolean> wizardFilledList;

    public WizardProfileFragmentVM(WizardProfileFragment fragment, FragmentWizardProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentWizardProfileBinding binding) {
        pager = binding.pager;
        tabLayout = binding.wizardTabLayout;
        agUser = new AgUser(getActivity());
        nextButton = binding.wizardAction;
        wizardFilledList = new ArrayMap<>();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setRxEventsBusListener();
        nextButton.setOnClickListener(v -> {
            doNext();
        });
        String[] profileFragment = new String[]{"avatar", "email", "updatePersonal", "family", "birthdaykids", "registration", "residence", "work", "updateSocial", "bindToPGU"};
        list = new ArrayMap<>();
//        list.add(new MakeAvatarFragment());
//        list.add(EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL));
//        list.add(new WizardPersonalDataFragment());
//        list.add(new WizardFamilyFragment());
//        list.add(EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.BIRTHDAY_KIDS));
//        list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_REGISTRATION));
//        list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_RESIDENCE));
//        list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_WORK));
//        list.add(BindingSocialFragment.newInstance(true));
//        list.add(PguAuthFragment.newInstanceForWizard());
        list.put("avatar", new MakeAvatarFragment());
        list.put("email", EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL));
        list.put("updatePersonal", new WizardPersonalDataFragment());
        list.put("family", new WizardFamilyFragment());
        list.put("birthdaykids", EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.BIRTHDAY_KIDS));
        list.put("registration", WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_REGISTRATION));
        list.put("residence", WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_RESIDENCE));
        list.put("work", WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_WORK));
        list.put("updateSocial", BindingSocialFragment.newInstance(true));
        list.put("bindToPGU", PguAuthFragment.newInstanceForWizard());
        for (int i = 0; i < list.size(); i++) {
            wizardFilledList.put(profileFragment[i], false);
        }
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
                    boolean currWizardFilled = wizardFilledList.get(list.keyAt(tab.getPosition()));
                    if (currWizardFilled) {
                        ImageView dot = (ImageView) tab.getCustomView().findViewById(R.id.wizard_dot);
                        Drawable myDrawable = getFragment().getResources().getDrawable(R.drawable.wizard_profile_default_dot);
                        dot.setImageDrawable(myDrawable);
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void setRxEventsBusListener() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.WizardEvents) {
                        Events.WizardEvents events = (Events.WizardEvents) o;
                        switch (events.getWizardType()) {
                            case Events.WizardEvents.WIZARD_AVATAR:
                                wizardFilledList.put("avatar", true);
                                break;
                            case Events.WizardEvents.WIZARD_EMAIL:
                                wizardFilledList.put("email", true);
                                break;
                            case Events.WizardEvents.WIZARD_PERSONAL:
                                break;
                            case Events.WizardEvents.WIZARD_FAMALY:
                                break;
                        }
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
//        list.add(index, fragment);
        adapter.notifyDataSetChanged();
        setDotCustomView();
    }

    public void doNext() {
        Fragment bd = list.get(pager.getCurrentItem());
        if (bd instanceof NavigateFragment) {
            NavigateFragment wpdf = (NavigateFragment) bd;
            wpdf.doRequestAction();
        }
    }

    public View getTabView() {
        return LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.item_wizard_profile_dot, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
