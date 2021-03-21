package org.rm3l.maoni.sample.utils

import android.content.Context
import android.preference.PreferenceManager
import org.rm3l.maoni.Maoni
import org.rm3l.maoni.common.contract.Handler
import org.rm3l.maoni.sample.extensions.getMaoniFeedbackBuilder
import org.rm3l.maoni.sample.extensions.getMaoniFeedbackHandler

object MaoniUtils {

    @JvmStatic
    fun buildMaoni(context: Context): Maoni {
        val handlerForMaoni: Handler = context.getMaoniFeedbackHandler() //Custom handler for Maoni, which does nothing more than calling any of the maoni-* available callbacks
        val maoniBuilder: Maoni.Builder = context.getMaoniFeedbackBuilder()
        // MaoniActivity de-registers handlers, listeners and validators upon activity destroy,
        // so we need to re-register it again by reconstructing a new Maoni instance.
        // Also, Maoni.start(...) cannot be called twice,
        // but we are reusing the Builder to construct a new instance along with its handler.
        //
        //Note that if no handler/listener is specified,
        //Maoni will fall back to opening an Email Intent, so your users can send
        //their feedback via email
        // MaoniActivity de-registers handlers, listeners and validators upon activity destroy,
        // so we need to re-register it again by reconstructing a new Maoni instance.
        // Also, Maoni.start(...) cannot be called twice,
        // but we are reusing the Builder to construct a new instance along with its handler.
        //
        //Note that if no handler/listener is specified,
        //Maoni will fall back to opening an Email Intent, so your users can send
        //their feedback via email
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return maoniBuilder
                .withScreenCapturingFeature(defaultSharedPreferences
                        .getBoolean("maoni_screen_capturing_enabled", true))
                .withLogsCapturingFeature(defaultSharedPreferences
                        .getBoolean("maoni_logs_capturing_enabled", true))
                .withHandler(handlerForMaoni).build()
    }
}
