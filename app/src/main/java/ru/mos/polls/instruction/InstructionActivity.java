package ru.mos.polls.instruction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;

/**
 * Экран "Как это работает?", представляет собой статичное описание возможностей приложения
 * задаваемое массивом блоков описания {@link ru.mos.polls.instruction.Instruction}
 *
 * @since 1.8
 */
public class InstructionActivity extends ToolbarAbstractActivity {
    @BindView(R.id.container)
    LinearLayout container;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, InstructionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(this);
        TitleHelper.setTitle(this, R.string.title_instruction);
        displayInstructions();
    }

    private void displayInstructions() {
        container.removeAllViews();
        for (Instruction i : Instruction.DEFAULT) {
            View v = View.inflate(this, R.layout.item_instruction, null);
            ImageView image = ButterKnife.findById(v, R.id.image);
            TextView title = ButterKnife.findById(v, R.id.title);
            TextView body = ButterKnife.findById(v, R.id.body);

            image.setImageResource(i.getResource());
            title.setText(getString(i.getTitle()));
            body.setText(getString(i.getBody()));
            container.addView(v);
        }
    }
}
