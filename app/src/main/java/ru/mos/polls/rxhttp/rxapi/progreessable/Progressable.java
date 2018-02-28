package ru.mos.polls.rxhttp.rxapi.progreessable;

/**
 * Супертип,определяющий поведение прогресса при загрузке
 *
 * @since 1.0
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
