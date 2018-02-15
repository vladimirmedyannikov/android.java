package ru.mos.polls.electronichouse.vm;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutElectronicHouseBinding;
import ru.mos.polls.electronichouse.ui.fragment.ElectronicHouseFragment;
import ru.mos.polls.electronichouse.ui.fragment.HouseAddressFragment;
import ru.mos.polls.electronichouse.ui.fragment.HousePollFragment;
import ru.mos.polls.profile.ui.adapter.PagerAdapter;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 01.12.17.
 */

public class ElectronicHouseFragmentVM extends UIComponentFragmentViewModel<ElectronicHouseFragment, LayoutElectronicHouseBinding> {

    private ViewPager pager;
    private TabLayout slidingTabs;
    private List<PagerAdapter.Page> list;

    public ElectronicHouseFragmentVM(ElectronicHouseFragment fragment, LayoutElectronicHouseBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutElectronicHouseBinding binding) {
        getActivity().setTitle(R.string.mainmenu_electronic_house);
        List<PagerAdapter.Page> pages = getPages();
        pager = binding.pager;
        PagerAdapter adapter = new PagerAdapter(getFragment().getChildFragmentManager(), pages);
        pager.setAdapter(adapter);

        slidingTabs = binding.slidingTabs;
        slidingTabs.setupWithViewPager(pager);
        for (int index = 0; index < pages.size(); ++index) {
            slidingTabs
                    .getTabAt(index)
                    .setText(getActivity().getString(pages.get(index).getTitleResId()));
        }
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    protected List<PagerAdapter.Page> getPages() {
        list = new ArrayList<>();
        list.add(new PagerAdapter.Page(HousePollFragment.newInstance(), R.string.polls_title));
        list.add(new PagerAdapter.Page(HouseAddressFragment.newInstance(), R.string.my_addresses_title));
        return list;
    }
}
