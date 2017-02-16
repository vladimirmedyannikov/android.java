package ru.mos.polls.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;

public class TutorialPageFragment extends Fragment {
    private static final String ARG_TUTORIAL = "arg_tutorial";

    public static Fragment newInstance(Tutorial tutorialItem) {
        TutorialPageFragment result = new TutorialPageFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(ARG_TUTORIAL, tutorialItem);
        result.setArguments(args);
        return result;
    }

    @BindView(R.id.tutorial_text)
    TextView textView;
    @BindView(R.id.tutorial_image)
    ImageView image;

    private Tutorial tutorialItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialItem = (Tutorial) getArguments().getSerializable(ARG_TUTORIAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tutorialFill();
    }

    private void tutorialFill() {
        image.setImageDrawable(getResources().getDrawable(tutorialItem.getImageDrawableId()));
        textView.setText(tutorialItem.getDescriptionDrawableId());
    }

}
