package ru.mos.polls.wizardprofile.vm;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.newprofile.ui.fragment.PguAuthFragment;
import ru.mos.polls.newprofile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.newprofile.vm.NewFlatFragmentVM;
import ru.mos.polls.profile.gui.fragment.BindingSocialFragment;
import ru.mos.polls.wizardprofile.ui.adapter.WizardProfilePagerAdapter;
import ru.mos.polls.wizardprofile.ui.fragment.WizardAvatarFragment;
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
    List<Fragment> list;
    List<String> tagFr;
    SparseBooleanArray frViewedList;
    WizardProfilePagerAdapter adapter;
    ArrayMap<String, Boolean> wizardFilledList;
    static final String AVATAR = "avatar";
    static final String EMAIL = "updateEmail";
    static final String PERSONAL = "updatePersonal";
    static final String FAMILY = "updateFamilyInfo";
    static final String LOCATION = "updateLocation";
    static final String EXTRAINFO = "updateExtraInfo";
    static final String SOCIAL = "updateSocial";
    static final String PGU = "bindToPGU";


    static final String TAG_BIRTHDAYKIDS = "birthdaykids";
    static final String TAG_REGISTRATION = "registration";
    static final String TAG_RESIDENCE = "residence";

    boolean isLastPage;
    int listSize;

    /*
    updatePersonal
   updateFamilyInfo
   updateEmail
   updateLocation
   updateExtraInfo
   updateSocial
   bindToPGU
   avatar - установка аватарки пользователя
     */
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
        ArrayList<String> mockIds = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.profile_sub_ids)));
        list = new ArrayList<>();
        tagFr = new ArrayList<>();
        frViewedList = new SparseBooleanArray();
        setFragmentListByIds(mockIds, list, tagFr);


        for (int i = 0; i < tagFr.size(); i++) {
            wizardFilledList.put(tagFr.get(i), false);
            frViewedList.put(i, false);
        }
        listSize = list.size();
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
                int tabPos = tab.getPosition() + 1;
                if (tabPos == listSize) {
                    isLastPage = true;
                }
                if (tabPos < listSize) {
                    if (isLastPage) {
                        nextButton.setText("Продолжить");
                    }
                    isLastPage = false;
                }
                if (isLastPage) {
                    nextButton.setText("Завершить");
                }
                try {
                    setDotColor(tab, R.drawable.wizard_profile_selected_dot);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                frViewedList.put(tab.getPosition(), true);
                try {
                    boolean preWizardFr = wizardFilledList.get(tagFr.get(tab.getPosition()));
                    if (preWizardFr) {
                        setDotColor(tab, R.drawable.wizard_profile_default_dot);
                    } else {
                        setDotColor(tab, R.drawable.wizard_profile_warning_dot);
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

    public void setFragmentListByIds(ArrayList<String> mockIds, List<Fragment> frList, List<String> tagList) {
        if (mockIds.contains(AVATAR)) {
            frList.add(new WizardAvatarFragment());
            tagList.add(AVATAR);
        }
        if (mockIds.contains(EMAIL)) {
            frList.add(EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.PERSONAL_EMAIL));
            tagList.add(EMAIL);
        }
        if (mockIds.contains(PERSONAL)) {
            list.add(new WizardPersonalDataFragment());
            tagList.add(PERSONAL);
        }
        if (mockIds.contains(FAMILY)) {
            list.add(new WizardFamilyFragment());
            tagList.add(FAMILY);
        }
        if (mockIds.contains(LOCATION)) {
            list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_REGISTRATION));
            list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_RESIDENCE));
            tagList.add(TAG_REGISTRATION);
            tagList.add(TAG_RESIDENCE);
        }
        if (mockIds.contains(EXTRAINFO)) {
            list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_WORK));
            tagList.add(EXTRAINFO);
        }
        if (mockIds.contains(SOCIAL)) {
            list.add(BindingSocialFragment.newInstance(true));
            tagList.add(SOCIAL);
        }
        if (mockIds.contains(PGU)) {
            list.add(PguAuthFragment.newInstanceForWizard());
            tagList.add(PGU);
        }
    }

    public void setRxEventsBusListener() {
        AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof Events.WizardEvents) {
                        Events.WizardEvents events = (Events.WizardEvents) o;
                        agUser = new AgUser(getActivity());
                        switch (events.getWizardType()) {
                            case Events.WizardEvents.WIZARD_AVATAR:
                                wizardFilledList.put(AVATAR, true);
                                break;
                            case Events.WizardEvents.WIZARD_EMAIL:
                                wizardFilledList.put(EMAIL, true);
                                break;
                            case Events.WizardEvents.WIZARD_PERSONAL:
                                wizardFilledList.put(PERSONAL, true);
                                break;
                            case Events.WizardEvents.WIZARD_FAMALY:
                                wizardFilledList.put(FAMILY, true);
                                checkBirthdayKidsFr();
                                break;
                            case Events.WizardEvents.WIZARD_KIDS:
                                wizardFilledList.put(TAG_BIRTHDAYKIDS, true);
                                break;
                            case Events.WizardEvents.WIZARD_REGISTRATION:
                                wizardFilledList.put(TAG_REGISTRATION, true);
                                break;
                            case Events.WizardEvents.WIZARD_RESIDENCE:
                                wizardFilledList.put(TAG_RESIDENCE, true);
                                break;
                            case Events.WizardEvents.WIZARD_WORK:
                                wizardFilledList.put(EXTRAINFO, true);
                                break;
                            case Events.WizardEvents.WIZARD_SOCIAL:
                                wizardFilledList.put(SOCIAL, true);
                                break;
                            case Events.WizardEvents.WIZARD_PGU:
                                wizardFilledList.put(PGU, true);
                                break;
                        }
                    }
                });
    }

    public void checkPageNumber() {
        int nextPage = pager.getCurrentItem() + 1;
        if (nextPage <= listSize) {
            pager.setCurrentItem(nextPage);
            if (nextPage == listSize) {
                nextButton.setText("Завершить");
            } else {
                nextButton.setText("Продолжить");
            }
        }
    }

    public void checkBirthdayKidsFr() {
        if (agUser.getChildCount() > 0) {
            int nextPosition = pager.getCurrentItem() + 1;
            if (!tagFr.contains(TAG_BIRTHDAYKIDS)) {
                addFragment(nextPosition, EditPersonalInfoFragment.newInstanceForWizard(agUser, EditPersonalInfoFragmentVM.BIRTHDAY_KIDS));
                tagFr.add(nextPosition, TAG_BIRTHDAYKIDS);
                wizardFilledList.put(TAG_BIRTHDAYKIDS, false);
                frViewedList.put(frViewedList.size() + 1, false);
                reDrawDots();
            } else {
                EditPersonalInfoFragment epif = (EditPersonalInfoFragment) list.get(nextPosition);
                epif.getViewModel().setAgUser(agUser);
                epif.getViewModel().setView(EditPersonalInfoFragmentVM.BIRTHDAY_KIDS);
            }
        }
        if (tagFr.contains(TAG_BIRTHDAYKIDS) && agUser.getChildCount() == 0) {
            int nextPosition = pager.getCurrentItem() + 1;
            removeFragment(nextPosition);
            tagFr.remove(TAG_BIRTHDAYKIDS);
            wizardFilledList.remove(TAG_BIRTHDAYKIDS);
            frViewedList.delete(nextPosition);
            reDrawDots();
        }
    }

    public void reDrawDots() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            boolean isViewed = frViewedList.get(i);
            String tag = tagFr.get(i);
            boolean isFilled = wizardFilledList.get(tag);
            if (isViewed && !isFilled) {
                setDotColor(tab, R.drawable.wizard_profile_warning_dot);
            }
        }
    }

    public void setDotCustomView() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(getTabView());
        }
    }

    public void removeFragment(int index) {
        list.remove(index);
        listSize = list.size();
        adapter.notifyDataSetChanged();
        setDotCustomView();
    }

    public void addFragment(int index, Fragment fragment) {
        list.add(index, fragment);
        listSize = list.size();
        adapter.notifyDataSetChanged();
        setDotCustomView();
    }

    public void setDotColor(TabLayout.Tab tab, @DrawableRes int res) {
        ImageView dot = (ImageView) tab.getCustomView().findViewById(R.id.wizard_dot);
        Drawable myDrawable = getActivity().getResources().getDrawable(res);
        dot.setImageDrawable(myDrawable);
    }

    public void doNext() {
        Fragment bd = list.get(pager.getCurrentItem());
        if (bd instanceof NavigateFragment) {
            NavigateFragment wpdf = (NavigateFragment) bd;
            wpdf.doRequestAction();
        }
        if (isLastPage) {
            if (wizardFilledList.containsValue(false)) {
                Toast.makeText(getActivity(), "Вы не до конца заполнили профиль", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().finish();
            }
        }
    }

    public View getTabView() {
        return LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.item_wizard_profile_dot, null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment currFr = list.get(pager.getCurrentItem());
        currFr.onActivityResult(requestCode, resultCode, data);
    }
}
