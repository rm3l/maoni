package org.rm3l.maoni.sample;

import org.rm3l.maoni.Maoni;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class MaoniSampleMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maoni_sample_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.setTitleTextAppearance(getApplicationContext(),
                    R.style.ToolbarTitle);
            toolbar.setSubtitleTextAppearance(getApplicationContext(),
                    R.style.ToolbarSubtitle);
            toolbar.setTitleTextColor(ContextCompat.getColor(this,
                    R.color.white));
            toolbar.setSubtitleTextColor(ContextCompat.getColor(this,
                    R.color.white));
            setSupportActionBar(toolbar);
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Maoni<>(MyFeedbackActivity.class)
                            .windowTitle("Feedback") //Set to an empty string to clear it
                            .message(null) //Use the default. Set to an empty string to clear it
                            .start(MaoniSampleMainActivity.this);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maoni_sample, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_about:
                new LibsBuilder()
                        //Pass the fields of your application to the lib so it can find all external lib information
                        .withFields(R.string.class.getFields())
                        .withActivityTitle(getString(R.string.action_about))
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        //start the activity
                        .start(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
