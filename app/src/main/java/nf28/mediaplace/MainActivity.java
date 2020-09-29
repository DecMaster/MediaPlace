package nf28.mediaplace;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import nf28.mediaplace.Controllers.Toolbox;
import nf28.mediaplace.Models.Film;
import nf28.mediaplace.Models.Jeu;
import nf28.mediaplace.Models.Livre;
import nf28.mediaplace.Models.Serie;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_books, R.id.nav_series, R.id.nav_movies, R.id.nav_options, R.id.nav_videoGames, R.id.nav_work, R.id.nav_share, R.id.nav_sharePreview, R.id.nav_search)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // CREATION TOOLBOX & DATA
        Toolbox.instance.actionBar = getSupportActionBar();
        Toolbox.instance.toolbar = findViewById(R.id.toolbar);
        Toolbox.instance.appPath = getFilesDir() + "/";
        Toolbox.instance.InitSampleData();
        Toolbox.instance.activity = this;

        Toolbox.instance.tabsColor.put(Film.class, getResources().getColor(R.color.colorMovieBlue));
        Toolbox.instance.tabsColor.put(Jeu.class, getResources().getColor(R.color.colorVideoGamesRed));
        Toolbox.instance.tabsColor.put(Livre.class, getResources().getColor(R.color.colorBookYellow));
        Toolbox.instance.tabsColor.put(Serie.class, getResources().getColor(R.color.colorShowGreen));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toolbox.instance.userBiblio.saveAll();
    }
}
