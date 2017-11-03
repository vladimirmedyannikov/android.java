package ru.mos.polls.base.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.gui.JugglerNavigationFragment;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.about.state.AboutAppState;
import ru.mos.polls.util.GuiUtils;


public class NavigationFragmentJuggler extends JugglerNavigationFragment {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 404404;

    public static JugglerFragment create(int itemIndex) {
        NavigationFragmentJuggler f = new NavigationFragmentJuggler();
        Bundle b = new Bundle();
        addSelectedItemToBundle(b, itemIndex);
        f.setArguments(b);
        return f;
    }

    public static boolean navigateVoid(BaseActivity context, int id) {
        boolean r;
        switch (id) {
            case R.id.menu_quests:

                r = true;
                break;
            case R.id.menu_survey:

                r = true;
                break;
            case R.id.menu_novelty:

                r = true;
                break;
            case R.id.menu_news:

                r = true;
                break;
            case R.id.menu_shop:

                r = true;
                break;
            case R.id.menu_points:

                r = true;
                break;
            case R.id.menu_profile:

                r = true;
                break;
            case R.id.menu_friend:

                r = true;
                break;
            case R.id.menu_settings:

                r = true;
                break;
            case R.id.menu_support:
                navigate(context, new AboutAppState(null));
                r = true;
                break;
            default:
                r = false;
        }
        return r;
    }

    private NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(getDefaultSelectedItem());
        GuiUtils.hideKeyboard(getView());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDrawerLayout().setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                GuiUtils.hideKeyboard(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        navigationView.inflateMenu(R.menu.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                boolean r;
                r = navigateVoid((BaseActivity)getActivity(), item.getItemId());
                if (r) {
                    close();
                }
                return r;
            }
        });
    }

    private static void navigate(BaseActivity context, State state){
        if (context.getJuggler().getLayoutId() == me.ilich.juggler.R.layout.juggler_layout_content_toolbar_navigation){
            context.navigateTo().state(Remove.all(), Add.deeper(state));
        } else {
            context.navigateTo().state(Remove.closeCurrentActivity(), Add.newActivity(state, BaseActivity.class));
        }
    }

}
