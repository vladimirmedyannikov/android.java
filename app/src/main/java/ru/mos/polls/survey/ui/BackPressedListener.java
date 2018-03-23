package ru.mos.polls.survey.ui;



public interface BackPressedListener {
    BackPressedListener STUB = new BackPressedListener() {
        @Override
        public void onBack() {
        }

        @Override
        public void onUp() {

        }

        @Override
        public void onLocationUpdated() {
        }
    };

    void onBack();

    void onUp();

    void onLocationUpdated();
}
