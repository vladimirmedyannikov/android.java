package ru.mos.polls.rxhttp.rxapi.progreessable;

/**
 * Супертип,определяющий поведение прогресса при загрузке
 *
 * @since 1.0
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.10.15 12:37.
 */
public interface Progressable {

    Progressable STUB = new Progressable() {

        @Override
        public void begin() {
        }

        @Override
        public void end() {
        }

    };

    void begin();

    void end();

}
