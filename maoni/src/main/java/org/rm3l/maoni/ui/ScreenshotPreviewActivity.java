package org.rm3l.maoni.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.rm3l.maoni.R;
import org.rm3l.maoni.utils.ViewUtils;

/**
 * Created by rm3l on 12/05/16.
 */
public class ScreenshotPreviewActivity extends AppCompatActivity {

    public static final String FILE_PATH = "SCRRENSHOT_FILE_PATH";

    private boolean mToolbarVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_AppBarOverlay);

        setContentView(R.layout.maoni_screenshot_preview_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.maoni_screenshot_preview_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(""); //No title
            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        final ImageView previewImage = (ImageView) findViewById(R.id.maoni_screenshot_preview_image);
        final TextView errorTextView = (TextView) findViewById(R.id.maoni_screenshot_preview_error);

        final String filePath = getIntent().getStringExtra(FILE_PATH);
        if (filePath != null) {
            errorTextView.setVisibility(View.GONE);
            previewImage.setVisibility(View.VISIBLE);
            previewImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
        } else {
            previewImage.setVisibility(View.GONE);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(R.string.maoni_screenshot_preview_internal_error);
        }

        mToolbarVisible = true;

        final View container = findViewById(R.id.maoni_screenshot_preview_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToolbarVisible) {
                    ViewUtils.hideToolbar(toolbar);
                    mToolbarVisible = false;
                } else {
                    ViewUtils.showToolbar(toolbar);
                    mToolbarVisible = true;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
