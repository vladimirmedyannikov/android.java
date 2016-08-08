package ru.mos.polls.badge;

public class Constants {

    /**
     * Задержка перед первым запуском
     */
    public static final int INIT_INTERVAL = 0;

    /**
     * Интервал перезапроса бейджей
     */
    public static final int RELOAD_SERVER_INTERVAL = 5 * 60 * 1000; //раз в 5 минут

    public static final int NEWS_UPLOAD_DELAY = 3 * 1000; //3 секунды

    public static final long RELOAD_MEMORY_INTERVAL = 1 * 1000;
}
