package ru.mos.polls.profile.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.profile.gui.activity.UpdateSocialActivity;
import ru.mos.polls.quests.QuestsFragment;
import ru.mos.polls.survey.hearing.controller.PguUIController;

/**
 * Экран для привязки данных аг к данным портала городских услуг, социальным сетям
 *
 * @since 1.9
 */
public class ProfileBindItemsFragment extends AbstractProfileFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile_bind_items, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.bindPgu)
    void bingPgu() {
        PguUIController.startPguBinding(ProfileBindItemsFragment.this);
    }

    @OnClick(R.id.bindSocial)
    void bingSocial() {
        Intent intent = new Intent(getActivity(), UpdateSocialActivity.class);
        intent.putExtra(MainActivity.IS_TASK, QuestsFragment.socialQuestIsAvaible());
        startActivity(intent);
    }
}
