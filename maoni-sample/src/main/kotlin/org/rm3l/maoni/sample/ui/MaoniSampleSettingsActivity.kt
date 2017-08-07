package org.rm3l.maoni.sample.ui

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.find
import org.rm3l.maoni.Maoni
import org.rm3l.maoni.common.contract.Handler
import org.rm3l.maoni.sample.R
import org.rm3l.maoni.sample.extensions.getMaoniFeedbackBuilder
import org.rm3l.maoni.sample.extensions.getMaoniFeedbackHandler

class MaoniSampleSettingsActivity: AppCompatActivity() {

  private var mMaoni: Maoni? = null
  private lateinit var mHandlerForMaoni: Handler
  private lateinit var mMaoniBuilder: Maoni.Builder

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_maoni_sample_settings)

    mHandlerForMaoni = getMaoniFeedbackHandler() //Custom handler for Maoni, which does nothing more than calling any of the maoni-* available callbacks
    mMaoniBuilder = getMaoniFeedbackBuilder()

    val toolbar = find<Toolbar>(R.id.toolbar)
    toolbar.setTitle(R.string.maoni_settings_activity_name)
    setSupportActionBar(toolbar)
    val actionBar = supportActionBar
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true)
      actionBar.setHomeButtonEnabled(true)
      actionBar.setHomeButtonEnabled(true)
    }
    fragmentManager.beginTransaction().replace(R.id.maoni_sample_settings_content,
        SettingsFragment()).commit()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_maoni_sample_settings, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
      R.id.action_feedback -> {
        // MaoniActivity de-registers handlers, listeners and validators upon activity destroy,
        // so we need to re-register it again by reconstructing a new Maoni instance.
        //Also, Maoni.start(...) cannot be called twice,
        // but we are reusing the Builder to construct a new instance along with its handler.
        //
        //Note that if no handler/listener is specified,
        //Maoni will fall back to opening an Email Intent, so your users can send
        //their feedback via email
        mMaoni = mMaoniBuilder
            .withScreenCapturingFeature(this.defaultSharedPreferences.getBoolean("maoni_screen_capturing_enabled", true))
            .withLogsCapturingFeature(this.defaultSharedPreferences.getBoolean("maoni_logs_capturing_enabled", true))
            .withHandler(mHandlerForMaoni).build()
        mMaoni?.start(this@MaoniSampleSettingsActivity)
        return true
      }
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    //Clear strong references used in Maoni, by de-registering any handlers, listeners and validators
    mMaoni?.clear()
    super.onDestroy()
  }
}

class  SettingsFragment: PreferenceFragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.maoni_sample_preferences)
  }
}