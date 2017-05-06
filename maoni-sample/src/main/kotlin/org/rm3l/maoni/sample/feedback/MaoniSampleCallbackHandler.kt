package org.rm3l.maoni.sample.feedback

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast
import org.jetbrains.anko.selector
import org.jetbrains.anko.warn
import org.rm3l.maoni.common.contract.Handler
import org.rm3l.maoni.common.contract.Listener
import org.rm3l.maoni.common.model.Feedback
import org.rm3l.maoni.email.MaoniEmailListener
import org.rm3l.maoni.github.MaoniGithubListener
import org.rm3l.maoni.sample.BuildConfig
import org.rm3l.maoni.sample.R

/**
 * Example of Callback for Maoni, written in a different JVM language:
 * Kotlin here, for conciseness and productivity.
 * <p>
 * This is a demo callback, that lets the users pick one of the common listeners available:
 * - maoni-email, for sending feedback via email
 * - maoni-github, for sending feedback as a Github issue (in the specified repo)
 */
class MaoniSampleCallbackHandler(val context: Context) : Handler, AnkoLogger {

  private var mEmailInputLayout: TextInputLayout? = null
  private var mEmail: EditText? = null
  private var mExtraEditText: EditText? = null
  private var mExtraRadioGroup: RadioGroup? = null

  override fun onDismiss() {
    context.longToast("Activity Dismissed")
  }

  override fun onCreate(rootView: View?, savedInstanceState: Bundle?) {
    mEmailInputLayout = rootView?.find(R.id.extra_email_inputlayout)
    mEmail = rootView?.find(R.id.extra_email)
    mExtraEditText = rootView?.find(R.id.extra_edittext)
    mExtraRadioGroup = rootView?.find(R.id.extra_radiogroup)
    mEmail?.setText(R.string.default_email_addr, TextView.BufferType.EDITABLE)
  }

  override fun validateForm(rootView: View?): Boolean {
    val emailText = mEmail?.text
    if (TextUtils.isEmpty(emailText)) {
      mEmailInputLayout?.let {
        it.isErrorEnabled = true
        it.error = context.getString(R.string.maoni_validate_must_not_be_blank)
      }
      return false
    } else {
      mEmailInputLayout?.let {
        it.isErrorEnabled = false
      }
    }
    return true
  }

  override fun onSendButtonClicked(feedback: Feedback?): Boolean {
    // Depending on your use case, you may add specific data in the feedback object returned,
    // and manipulate it accordingly
    feedback?.put("Email", mEmail?.text ?: "???")
    feedback?.put("My Extra Edit Text", mExtraEditText?.text ?: "")

    val myExtraRadioGroupChecked = when (mExtraRadioGroup?.checkedRadioButtonId) {
      R.id.extra_rg1 -> "RG 1"
      R.id.extra_rg2 -> "RG 2"
      R.id.extra_rg3 -> "RG 3"
      else -> null
    }
    feedback?.put("My Extra Radio Group", myExtraRadioGroupChecked ?: "???")

    if (TextUtils.isEmpty(BuildConfig.GITHUB_USERNAME)
        || TextUtils.isEmpty(BuildConfig.GITHUB_PASSWORD_TOKEN)) {
      //maoni-github not configured properly
      warn { "maoni-github not configured properly. " +
          "Both 'github.username' and 'github.passwordToken' " +
          "properties missing from local.properties" }
    }

    val listOfListeners = listOf(
        "Send via email (maoni-email)",
        "Send as Github issue (maoni-github)")
    context.selector("How would you like to send your feedback?", listOfListeners) { position ->
      val progressDialog = context.indeterminateProgressDialog(listOfListeners[position])
      progressDialog.show()
      val listenerSelected: Listener?
      when (position) {
        0 -> {
          //maoni-email
          listenerSelected = MaoniEmailListener(
              context,
              "text/html",
              "[Maoni] Feedback from Maoni Sample App " +
                  "(${feedback?.appInfo?.applicationId}: ${feedback?.appInfo?.versionName})",
              null,
              null,
              arrayOf("apps+maoni@rm3l.org"),
              null,
              arrayOf("apps+maoni_sample@rm3l.org"))
        }
        1 -> {
          //maoni-github
          val githubRepoOwner = "rm3l"
          val githubRepo = "maoni"
          listenerSelected = MaoniGithubListener(context,
              BuildConfig.GITHUB_USERNAME,
              BuildConfig.GITHUB_PASSWORD_TOKEN,
              githubRepoOwner,
              githubRepo,
              true,
              "Please hold on...",
              "Submitting your feedback to Github repo: $githubRepoOwner/$githubRepo ...",
              "Maoni Sample App",
              null,
              null,
              arrayOf("feedback", "maoni-sample"),
              arrayOf("rm3l"),
              "Issue has been created in Github repo. Thank you for your feedback!",
              "An error happened - please try again later")
        }
        else -> {
          listenerSelected = null
        }
      }
      listenerSelected?.onSendButtonClicked(feedback)
      progressDialog.cancel()
    }

    return true
  }
}
