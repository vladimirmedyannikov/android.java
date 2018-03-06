package ru.mos.polls.fortesters;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.mos.polls.BuildConfig;
import ru.mos.polls.R;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;


public class TestersController {

    Menu menu;
    public static boolean isProdEnable;

    public TestersController(Menu menu) {
        this.menu = menu;
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            if (menu != null) {
                MenuItem test = menu.findItem(R.id.action_test);
                if (test != null) {
                    test.setVisible(true);
                }
            }
        }
    }

    public static void switchTestApi(int item, Context context) {
        if (item == R.id.action_test) {
            if (isProdEnable) {
                disableProd();
                Toast.makeText(context, "Прод выключен", Toast.LENGTH_SHORT).show();
            } else {
                enableProd();
                Toast.makeText(context, "Прод включен", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void enableProd() {
        isProdEnable = true;
        AgApiBuilder.setForTestProd(true);
    }

    public static void disableProd() {
        isProdEnable = false;
        AgApiBuilder.setForTestProd(false);
    }
}
