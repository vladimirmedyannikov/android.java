package ru.mos.polls.profile.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import ru.mos.elk.profile.AgSocialStatus;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.R;
import ru.mos.polls.helpers.FragmentHelper;
import ru.mos.polls.profile.gui.fragment.location.NewAddressActivity;

/**
 * Работа с дополнительными данными пользователя:
 * адрес работы, профессия
 *
 * @since 1.9
 */
public class WorkFragment extends AbstractProfileFragment {
    public static AbstractProfileFragment newInstance() {
        return new WorkFragment();
    }

    @BindView(R.id.socialStatus)
    Spinner socialStatus;
    private AgSocialStatus.Adapter adapter;
    @BindView(R.id.btnWork)
    TextView workView;
    private Flat work;
    private int selectedAgSocialStatus;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Flat newFlat = NewAddressActivity.onResult(requestCode, resultCode, data);
        if (newFlat != null) {
            work = newFlat;
            work.save(getActivity());
        }
        refreshUI();
        changeListener.onChange(WORK_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_work, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        refreshUI();
        AgUser agUser = new AgUser(getActivity());
        refreshUI(agUser);
    }

    @Override
    public void updateAgUser(AgUser agUser) {
        agUser.setWorkFlat(work)
                .setAgSocialStatus(selectedAgSocialStatus);
    }

    @Override
    public void refreshUI(AgUser agUser) {
        selectedAgSocialStatus = agUser.getAgSocialStatus();
        displaySocialStatus(agUser);
        work = agUser.getWork();
        refreshUI();
    }

    @Override
    public boolean isFilledAndChanged(AgUser old, AgUser changed) {
        boolean result = false;
        try {
            boolean isFilledWork = !changed.isEmptyWork();
            boolean isChangedWork = !changed.equalsWork(old);
            result = isFilledWork && isChangedWork /*&& isChangedWorkFlat*/;
        } catch (Exception ignored) {
        }
        return result;
    }

    private boolean isChanged(Flat old, Flat edited) {
        return (!edited.isEmpty() && !old.equals(edited))
                || (edited.isEmpty() && !old.isEmpty());
    }

    @OnClick(R.id.btnWork)
    void addWorkFlat() {
        Fragment fragment = FragmentHelper.getParentFragment(WorkFragment.this);
        if (fragment == null) {
            fragment = WorkFragment.this;
        }
        NewAddressActivity.startActivity(fragment, work);
    }

    private void refreshUI() {
        work = Flat.getWork(getActivity());
        workView.setText(work.getViewTitle(getActivity()));
        int drawable = work.isEmpty() ? R.drawable.ag_home : 0;
        workView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    private void displaySocialStatus(AgUser agUser) {
        adapter = (AgSocialStatus.Adapter) AgSocialStatus.getAdapter(getActivity());
        socialStatus.setAdapter(adapter);
        socialStatus.setSelection(agUser.getAgSocialStatus());
    }

    @OnItemSelected(R.id.socialStatus)
    void setSocialStatusListener(int position) {
        selectedAgSocialStatus = adapter.getItem(position).getId();
        changeListener.onChange(WORK_ID);
    }
}
