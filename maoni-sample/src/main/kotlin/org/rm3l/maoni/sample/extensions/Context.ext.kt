@file:JvmName("ContextUtils")

package org.rm3l.maoni.sample.extensions

import android.content.Context
import org.rm3l.maoni.Maoni
import org.rm3l.maoni.common.contract.Handler
import org.rm3l.maoni.sample.BuildConfig
import org.rm3l.maoni.sample.R
import org.rm3l.maoni.sample.feedback.MaoniSampleCallbackHandler

//Custom handler for Maoni, which does nothing more than calling any of the maoni-* available callbacks
fun Context.getMaoniFeedbackHandler(): Handler = MaoniSampleCallbackHandler(this)

fun Context.getMaoniFeedbackBuilder() =
  Maoni.Builder(this, "${BuildConfig.APPLICATION_ID}.fileprovider")
      .withSharedPreferences("${this.packageName}_preferences")
      .withWindowTitle(getString(R.string.send_feedback_activity_title)) //Set to an empty string to clear it
      .withMessage(getString(R.string.send_feedback_activity_intro))
      .withExtraLayout(R.layout.my_feedback_activity_extra_content)
      .withFeedbackContentHint(getString(R.string.feedback_content_hint))
      .withIncludeLogsText(getString(R.string.include_app_logs))
      .withIncludeScreenshotText(getString(R.string.include_app_screenshot))
      .withTouchToPreviewScreenshotText(getString(R.string.touch_edit_screenshot))
      .withContentErrorMessage(getString(R.string.content_error_message))
      .withDefaultToEmailAddress("apps+maoni_sample@rm3l.org")
      .withScreenshotHint(getString(R.string.screenshot_hint))
