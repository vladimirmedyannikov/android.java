package ru.mos.polls.instruction;

import ru.mos.polls.R;

/**
 * Структура данных, описывающая блок информации о том, что может приложение
 * Используется на экране "О том как это работает?" {@link ru.mos.polls.instruction.InstructionActivity}
 *
 * @since 1.9
 */
public class Instruction {
    private int resource;
    private int title;
    private int body;

    public static Instruction[] DEFAULT = new Instruction[]{
            new Instruction(R.drawable.big_icon_register, R.string.title_step_1, R.string.step_1),
            new Instruction(R.drawable.big_icon_get_ball_uslugi, R.string.title_step_2, R.string.step_2),
            new Instruction(R.drawable.big_icon_gor_uslugi, R.string.title_step_3, R.string.step_3)
    };


    public Instruction(int resource, int title, int body) {
        this.resource = resource;
        this.title = title;
        this.body = body;
    }

    public int getResource() {
        return resource;
    }

    public int getTitle() {
        return title;
    }

    public int getBody() {
        return body;
    }
}
