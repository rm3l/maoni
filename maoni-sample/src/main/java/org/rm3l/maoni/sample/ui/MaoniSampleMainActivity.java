/*
 * Copyright (c) 2016 Armel Soro
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.rm3l.maoni.sample.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.rm3l.maoni.Maoni;
import org.rm3l.maoni.common.contract.Handler;
import org.rm3l.maoni.sample.R;
import org.rm3l.maoni.sample.extensions.ContextUtils;

public class MaoniSampleMainActivity extends AppCompatActivity {

    private Maoni mMaoni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.maoni_sample_preferences, false);
        setContentView(R.layout.activity_maoni_sample_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.maoni_app_name);
            setSupportActionBar(toolbar);
        }

        final Handler handlerForMaoni = ContextUtils.getMaoniFeedbackHandler(this); //Custom handler for Maoni, which does nothing more than calling any of the maoni-* available callbacks
        final Maoni.Builder maoniBuilder = ContextUtils.getMaoniFeedbackBuilder(this);
        final FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // MaoniActivity de-registers handlers, listeners and validators upon activity destroy,
                    // so we need to re-register it again by reconstructing a new Maoni instance.
                    //Also, Maoni.start(...) cannot be called twice,
                    // but we are reusing the Builder to construct a new instance along with its handler.
                    //
                    //Note that if no handler/listener is specified,
                    //Maoni will fall back to opening an Email Intent, so your users can send
                    //their feedback via email
                  final SharedPreferences defaultSharedPreferences =
                      PreferenceManager.getDefaultSharedPreferences(MaoniSampleMainActivity.this);
                  mMaoni = maoniBuilder
                        .withScreenCapturingFeature(defaultSharedPreferences
                            .getBoolean("maoni_screen_capturing_enabled", true))
                        .withLogsCapturingFeature(defaultSharedPreferences
                            .getBoolean("maoni_logs_capturing_enabled", true))
                        .withHandler(handlerForMaoni).build();
                    mMaoni.start(MaoniSampleMainActivity.this);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        //Clear strong references used in Maoni, by de-registering any handlers, listeners and validators
        mMaoni.clear();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maoni_sample, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();

        } else if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, MaoniSampleSettingsActivity.class));

        } else if (itemId == R.id.action_about) {
            new LibsBuilder()
                    //Pass the fields of your application to the lib so it can find all external lib information
                    .withFields(R.string.class.getFields())
                    .withActivityTitle(getString(R.string.action_about))
                    //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    //start the activity
                    .start(this);

        }
        return super.onOptionsItemSelected(item);
    }
}
