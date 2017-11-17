package ru.mos.polls.wizardprofile.vm;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentWizardProfileBinding;
import ru.mos.polls.profile.ui.fragment.EditPersonalInfoFragment;
import ru.mos.polls.profile.ui.fragment.PguAuthFragment;
import ru.mos.polls.profile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.profile.vm.NewFlatFragmentVM;
import ru.mos.polls.profile.ui.fragment.BindingSocialFragment;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.wizardprofile.ui.adapter.WizardProfilePagerAdapter;
import ru.mos.polls.wizardprofile.ui.fragment.WizardAvatarFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFamilyFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardFlatFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardPersonalDataFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 27.07.2017.
 */

public class WizardProfileFragmentVM extends UIComponentFragmentViewModel<WizardProfileFragment, FragmentWizardProfileBinding> {


    ViewPager pager;
    TabLayout tabLayout;
    AgUser agUser;
    Button nextButton;
    List<Fragment> list;
    List<String> tagFr;
    SparseBooleanArray frViewedList;
    WizardProfilePagerAdapter adapter;
    ArrayMap<String, Boolean> wizardFilledList;
    AppCompatTextView percentegeTitle;
    ProgressBar profileProgressbar;

    static final String AVATAR = "avatar";
    static final String EMAIL = "updateEmail";
    static final String PERSONAL = "updatePersonal";
    static final String FAMILY = "updateFamilyInfo";
    static final String LOCATION_REGISTRATION = "updateLocationRegistration";
    static final String LOCATION_RESIDENCE = "updateLocationResidence";
    static final String EXTRAINFO = "updateExtraInfo";
    static final String SOCIAL = "updateSocial";
    static final String PGU = "bindToPGU";


    static final String TAG_BIRTHDAYKIDS = "birthdaykids";
    static final String TAG_REGISTRATION = "registration";
    static final String TAG_RESIDENCE = "residence";

    boolean isLastPage;
    int listSize;
    public List<String> ids;
    int percent;

    /*
    updatePersonal
   updateFamilyInfo
   updateEmail
   updateLocationRegistration
   updateLocationResidence
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
        percentegeTitle = binding.wizardPercentageValue;
        profileProgressbar = binding.wizardProfileProgressbar;
        agUser = new AgUser(getActivity());
        nextButton = binding.wizardAction;
        wizardFilledList = new ArrayMap<>();
        pager.setOffscreenPageLimit(2);
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            percent = extras.getInt(WizardProfileFragment.ARG_WIZARD_PERCENT);
            ids = new ArrayList<>();
            List<String> list = extras.getStringArrayList(WizardProfileFragment.ARG_WIZARD_IDS);
            ids.addAll(list);
            pager.setOffscreenPageLimit(list.size());
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setRxEventsBusListener();
        setPercentegeTitleView(percent);
        setProfileProgressbarView(percent);
        nextButton.setOnClickListener(v -> {
            doNext();
        });
        list = new ArrayList<>();
        tagFr = new ArrayList<>();
        frViewedList = new SparseBooleanArray();
        setFragmentListByIds(ids, list, tagFr);

        for (int i = 0; i < tagFr.size(); i++) {
            wizardFilledList.put(tagFr.get(i), false);
            frViewedList.put(i, false);
        }
        listSize = list.size();
        adapter = new WizardProfilePagerAdapter(getFragment().getChildFragmentManager(), list);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager, true);
        setDotCustomView();
        setTabListener();
        setNextButtonView(tabLayout.getSelectedTabPosition() + 1);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        ProgressableUIComponent progressableUIComponent = new ProgressableUIComponent();
        progressableUIComponent.setSendEvents(false);
        return new UIComponentHolder.Builder().with(progressableUIComponent).build();
    }

    public void setTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setNextButtonView(tab.getPosition() + 1);
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
                    if (tagFr.get(tab.getPosition()).equalsIgnoreCase(EMAIL) && !preWizardFr) {
                        setDotColor(tab, R.drawable.wizard_profile_warning_dot);
                        return;
                    }
//                    if (preWizardFr) {
                    setDotColor(tab, R.drawable.wizard_profile_default_dot);
//                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void setNextButtonView(int position) {
        if (position == listSize) {
            isLastPage = true;
        }
        if (position < listSize) {
            if (isLastPage) {
                nextButton.setText("Продолжить");
            }
            isLastPage = false;
        }
        if (isLastPage) {
            nextButton.setText("Завершить");
        }
    }

    public void setPercentegeTitleView(int percent) {
        percentegeTitle.setText(String.format("%d%%", percent));
    }

    public void setProfileProgressbarView(int percent) {
        profileProgressbar.setProgress(percent);
    }

    public void setFragmentListByIds(List<String> mockIds, List<Fragment> frList, List<String> tagList) {
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
        if (mockIds.contains(LOCATION_REGISTRATION)) {
            list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_REGISTRATION));
            tagList.add(TAG_REGISTRATION);
        }
        if (mockIds.contains(LOCATION_RESIDENCE)) {
            list.add(WizardFlatFragment.newInstance(agUser, NewFlatFragmentVM.FLAT_TYPE_RESIDENCE));
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
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {

                        if (o instanceof Events.WizardEvents) {
                            Events.WizardEvents events = (Events.WizardEvents) o;
                            agUser = new AgUser(getActivity());
                            percent = events.getPercentFillProfile();
                            switch (events.getEventType()) {
                                case Events.WizardEvents.WIZARD_AVATAR:
                                    wizardFilledList.put(AVATAR, true);
                                    break;
                                case Events.WizardEvents.WIZARD_EMAIL:
                                    wizardFilledList.put(EMAIL, false);
                                    break;
                                case Events.WizardEvents.WIZARD_PERSONAL:
                                    wizardFilledList.put(PERSONAL, false);
                                    break;
                                case Events.WizardEvents.WIZARD_FAMILY:
                                    wizardFilledList.put(FAMILY, false);
                                    checkBirthdayKidsFr();
                                    break;
                                case Events.WizardEvents.WIZARD_KIDS:
                                    wizardFilledList.put(TAG_BIRTHDAYKIDS, false);
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
                            if (events.getEventType() != Events.WizardEvents.WIZARD_UPDATE_GENDER
                                    && events.getEventType() != Events.WizardEvents.WIZARD_SOCIAL
                                    && events.getEventType() != Events.WizardEvents.WIZARD_CHANGE_FLAT_FR
                                    && events.getEventType() != Events.WizardEvents.WIZARD_SOCIAL_STATUS) {
                                slideNextPage();
                            }
                            setPercentegeTitleView(percent);
                            setProfileProgressbarView(percent);
                        }
                        if (o instanceof Events.ProgressableEvents) {
                            Events.ProgressableEvents events = (Events.ProgressableEvents) o;
                            switch (events.getEventType()) {
                                case Events.ProgressableEvents.BEGIN:
                                    if (getFragment() != null)
                                        GuiUtils.hideKeyboard(getFragment().getView());
                                    getComponent(ProgressableUIComponent.class).begin();
                                    break;
                                case Events.ProgressableEvents.END:
                                    getComponent(ProgressableUIComponent.class).end();
                                    break;
                            }
                        }
                    }
                }));
    }

    public void slideNextPage() {
        int nextPage = pager.getCurrentItem() + 1;
        if (nextPage <= listSize) {
            pager.setCurrentItem(nextPage);
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
                for (Fragment fragment : list) {
                    if (fragment instanceof EditPersonalInfoFragment) {
                        EditPersonalInfoFragment epif = (EditPersonalInfoFragment) fragment;
                        if (epif.getViewModel() != null && epif.getViewModel().getPersonalType() == EditPersonalInfoFragmentVM.BIRTHDAY_KIDS) {
                            epif.getViewModel().setAgUser(agUser);
                            epif.getViewModel().setView(EditPersonalInfoFragmentVM.BIRTHDAY_KIDS);
                            break;
                        }
                    }
                }
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
            if (tag.equalsIgnoreCase(EMAIL) && !isFilled) {
                setDotColor(tab, R.drawable.wizard_profile_warning_dot);
            }
//            if (isViewed && !isFilled) {
//            }
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
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setDotCustomView();
    }

    public void setDotColor(TabLayout.Tab tab, @DrawableRes int res) {
        ImageView dot = (ImageView) tab.getCustomView().findViewById(R.id.wizard_dot);
        Drawable myDrawable = getActivity().getResources().getDrawable(res);
        dot.setImageDrawable(myDrawable);
    }

    public void doNext() {
        Fragment bd = list.get(pager.getCurrentItem());
        boolean isCurrFrFilled = wizardFilledList.get(tagFr.get(pager.getCurrentItem()));
        if (bd instanceof NavigateFragment && !isCurrFrFilled) {
            NavigateFragment wpdf = (NavigateFragment) bd;
            wpdf.doRequestAction();
        } else {
            slideNextPage();
            return;
        }
        if (isLastPage) {
            getActivity().setResult(WizardProfileFragment.RESULT_CODE_START_PROFILE_FOR_INFO_PAGE);
            getActivity().finish();
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

    public boolean isFlatFlatFragment() {
        return list.get(pager.getCurrentItem()) instanceof WizardFlatFragment;
    }


    public boolean isCustomFlatFragment() {
        WizardFlatFragment fr = (WizardFlatFragment) list.get(pager.getCurrentItem());
        return fr.getViewModel().isCustomFlat;
    }

}
