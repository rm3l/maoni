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
import org.rm3l.maoni.jira.MaoniJiraListener
import org.rm3l.maoni.sample.BuildConfig
import org.rm3l.maoni.sample.R
import org.rm3l.maoni.slack.MaoniSlackListener

/**
 * Example of Callback for Maoni, written in a different JVM language:
 * Kotlin here, for conciseness and productivity.
 * <p>
 * This is a demo callback, that lets the users pick one of the common listeners available
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

    var maoniGithubConfigured: Boolean = true
    if (TextUtils.isEmpty(BuildConfig.GITHUB_USERNAME)
        || TextUtils.isEmpty(BuildConfig.GITHUB_PASSWORD_TOKEN)) {
      //maoni-github not configured properly
      warn { "maoni-github not configured properly. " +
          "Both 'github.username' and 'github.passwordToken' " +
          "properties missing from local.properties" }
      maoniGithubConfigured = false
    }

    var maoniJiraConfigured: Boolean = true
    if (TextUtils.isEmpty(BuildConfig.JIRA_REST_BASE_URL)
        || TextUtils.isEmpty(BuildConfig.JIRA_USERNAME)
        || TextUtils.isEmpty(BuildConfig.JIRA_PASSWORD)) {
      //maoni-jira not configured properly
      warn { "maoni-jira not configured properly. " +
          "'jira.rest.baseUrl', 'jira.username' and 'jira.password'" +
          "properties missing from local.properties" }
      maoniJiraConfigured = false
    }

    var maoniSlackConfigured: Boolean = true
    if (TextUtils.isEmpty(BuildConfig.SLACK_WEBHOOK_URL)) {
      warn { "maoni-slack not configured properly. " +
          "'slack.webhook.url' property missing from local.properties" }
      maoniSlackConfigured = false
    }

    val listOfListeners = mutableListOf(
        context.getString(R.string.send_maoni_email)
    )
    if (maoniGithubConfigured) {
      listOfListeners.add(context.getString(R.string.send_maoni_github))
    }
    if (maoniJiraConfigured) {
      listOfListeners.add(context.getString(R.string.send_maoni_jira))
    }
    if (maoniSlackConfigured) {
      listOfListeners.add(context.getString(R.string.send_maoni_slack))
    }

    context.selector(context.getString(R.string.how_to_send_feedback), listOfListeners) { position ->
      val textAtPosition = listOfListeners[position]
      val progressDialog = context.indeterminateProgressDialog(textAtPosition)
      progressDialog.show()
      val listenerSelected: Listener?

      if (textAtPosition.contains("maoni-email", true)) {
        listenerSelected = buildMaoniEmailListener(feedback)
      } else if (maoniGithubConfigured &&
          textAtPosition.contains("maoni-github", ignoreCase = true)) {
        listenerSelected = buildMaoniGithubListener()
      } else if (maoniJiraConfigured &&
          textAtPosition.contains("maoni-jira", ignoreCase = true)) {
        listenerSelected = buildMaoniJiraListener()
      } else if (maoniSlackConfigured &&
          textAtPosition.contains("maoni-slack", ignoreCase = true)) {
        listenerSelected = buildMaoniSlackListener()
      } else {
        listenerSelected = null
      }

      listenerSelected?.onSendButtonClicked(feedback)
      progressDialog.cancel()
    }

    return true
  }

  private fun buildMaoniEmailListener(feedback: Feedback?): Listener {
    return MaoniEmailListener(
        context,
        "text/html",
        "[Maoni] Feedback from Maoni Sample App " +
            "(${feedback?.appInfo?.applicationId ?: "UNKNOWN_APPLICATION_ID"}: " +
            "${feedback?.appInfo?.versionName ?: "UNKNOWN_VERSION_NAME"})",
        null,
        null,
        arrayOf("apps+maoni@rm3l.org"),
        null,
        arrayOf("apps+maoni_sample@rm3l.org"))
  }

  private fun buildMaoniGithubListener(): Listener {
    val githubRepoOwner = "rm3l"
    val githubRepo = "maoni"
    return MaoniGithubListener(context,
        BuildConfig.GITHUB_USERNAME,
        BuildConfig.GITHUB_PASSWORD_TOKEN,
        githubRepoOwner,
        githubRepo,
        BuildConfig.DEBUG,
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

  private fun buildMaoniJiraListener(): Listener {
    val jiraProjectKey = "MAONI"
    return MaoniJiraListener(
        context,
        BuildConfig.DEBUG,
        BuildConfig.JIRA_REST_BASE_URL,
        BuildConfig.JIRA_USERNAME,
        BuildConfig.JIRA_PASSWORD,
        jiraProjectKey,
        jiraIssueType = "Task",
        jiraIssueSummaryPrefix = "Maoni Sample App",
        successToastMessage = "Issue has been created in JIRA Project '$jiraProjectKey'. " +
            "Thank you for your feedback!",
        failureToastMessage = "An error happened - please try again later"
        )
  }

  private fun buildMaoniSlackListener(): Listener {
    return MaoniSlackListener(
        context = context,
        webhookUrl = BuildConfig.SLACK_WEBHOOK_URL,
        debug = BuildConfig.DEBUG,
        channel = if (BuildConfig.SLACK_CHANNEL.isNullOrBlank()) null else BuildConfig.SLACK_CHANNEL,
        username = if (BuildConfig.SLACK_USERNAME.isNullOrBlank()) null else BuildConfig.SLACK_USERNAME,
        iconUrl = if (BuildConfig.SLACK_ICON_URL.isNullOrBlank()) null else BuildConfig.SLACK_ICON_URL,
        emojiIcon = if (BuildConfig.SLACK_EMOJI_ICON.isNullOrBlank()) null else BuildConfig.SLACK_EMOJI_ICON,
        successToastMessage = "Feedback sent to Slack Webhook URL '${BuildConfig.SLACK_WEBHOOK_URL}'. " +
            "Thank you for your feedback!",
        failureToastMessage = "An error happened - please try again later"
        )
  }
}
