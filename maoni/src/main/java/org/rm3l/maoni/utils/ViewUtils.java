package org.rm3l.maoni.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by rm3l on 08/05/16.
 */
public final class ViewUtils {

    public static final int COMPRESSION_QUALITY = 100;
    public static final int DEFAULT_BITMAP_WIDTH = 640;
    public static final int DEFAULT_BITMAP_HEIGHT = 480;

    private ViewUtils() {
    }

    public static void hideAppBarLayout(@Nullable final AppBarLayout appBarLayout) {
        if (appBarLayout == null) {
            return;
        }
        appBarLayout.animate()
                .translationY(-appBarLayout.getHeight())
                .setInterpolator(new AccelerateInterpolator(2));
    }

    public static void showAppBarLayout(@Nullable final AppBarLayout appBarLayout) {
        if (appBarLayout == null) {
            return;
        }
        appBarLayout.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2));
    }

    @Nullable
    public static Bitmap toBitmap(@Nullable final View view) {
        if (view == null) {
            return null;
        }
        final int width = view.getWidth();
        final int height = view.getHeight();
        final Bitmap bitmapToExport = Bitmap
                .createBitmap(width > 0 ? width : DEFAULT_BITMAP_WIDTH,
                        height > 0 ? height : DEFAULT_BITMAP_HEIGHT,
                        Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapToExport);
        view.draw(canvas);
        return bitmapToExport;
    }

    public static void exportViewToFile(@NonNull final Context context,
                                        @NonNull final View view, @NonNull final File file) {
        final Bitmap bitmap = toBitmap(view);
        if (bitmap == null) {
            return;
        }
        exportBitmapToFile(context, bitmap, file);
    }

    public static void exportBitmapToFile(@NonNull final Context context,
                                          @NonNull final Bitmap bitmap, @NonNull final File file) {
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file, false));
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, outputStream);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //No Worries
            }
        }
    }
}
