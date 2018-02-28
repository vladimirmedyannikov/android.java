package ru.mos.polls.mypoints.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import ru.mos.polls.BR;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PromoControllerRX;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentNewMyPointsBinding;
import ru.mos.polls.helpers.ActionBarHelper;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.mypoints.vm.NewMyPointsFragmentVM;

public class NewMyPointsFragment extends BindingFragment<NewMyPointsFragmentVM, FragmentNewMyPointsBinding> {

    public static NewMyPointsFragment newInstance() {
        return new NewMyPointsFragment();
    }

    public NewMyPointsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.my_points);
        setHasOptionsMenu(true);
        Statistics.enterBonus();
        GoogleStatistics.AGNavigation.enterBonus();
    }

    @Override
    protected NewMyPointsFragmentVM onCreateViewModel(FragmentNewMyPointsBinding binding) {
        return new NewMyPointsFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Оставляем исопльзование кастомного экшен бара для этого экрана, т.к
         * иконка нестандартная
         */
        PromoControllerRX.PromoResponse listener = new PromoControllerRX.PromoResponse() {

            @Override
            public void onResponse() {
                getViewModel().clearList();
                getViewModel().doRequest();
            }

            @Override
            public void onError() {

            }
        };
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.shopPromoCode();
                GoogleStatistics.AGNavigation.shopPromoCode();
                PromoControllerRX.setPromoResponse(listener);
                PromoControllerRX.showInputDialog(disposable, getContext(), null);
            }
        };
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            View promoView = View.inflate(getActivity(), R.layout.view_promo, null);
            promoView.setOnClickListener(onClickListener);
            actionBar.setCustomView(promoView);
            Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) promoView.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
        }
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_new_my_points;
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBarHelper.hideCustomActionBar((AppCompatActivity) getActivity());
    }
}
