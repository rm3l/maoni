/*
 * Copyright (c) 2017 Armel Soro
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
package org.rm3l.maoni.slack

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import khttp.post
import org.jetbrains.anko.*
import org.rm3l.maoni.common.contract.Listener
import org.rm3l.maoni.common.model.Feedback
import java.nio.charset.Charset

/**
 * Callback for Maoni that takes care of sending the Feedback to specified Slack channels.
 * <p>
 * Written in Kotlin for conciseness
 */
const val LOG_TAG = "MaoniSlackListener"
const val USER_AGENT = "maoni-slack (v8.0.2)"
const val APPLICATION_JSON = "application/json"

/**
 * Maoni Listener that sends Feedback to Slack
 *
 * @property context the Android Context
 * @property webhookUrl the Slack Webhook URL, to set up here: https://my.slack.com/services/new/incoming-webhook
 * @property debug whether to display debug messages
 * @property channel the Slack Channel (overrides the default channel of the WebHook URL) where the Feedback will be posted.
 *  A public channel can be specified with "#other-channel", and a Direct Message with "@username"
 * @property username the displayed name
 * @property iconUrl the icon to display
 * @property emojiIcon the emoji icon to display
 * @property showWaitDialog whether or not to display a wait dialog. If false,
 *  the waitDialogTitle  and waitDialogMessage properties will be useless.
 * @property waitDialogTitle the message to display in the waiting dialog
 * @property successToastMessage the message to toast if the operation succeeded
 * @property failureToastMessage the message to toast in case of failure
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class MaoniSlackListener @JvmOverloads constructor(
        val context: Context,
        val webhookUrl: String,
        val debug: Boolean = false,
        val channel: String? = null,
        val username: String? = null,
        val iconUrl: String? = null,
        val emojiIcon: String? = null,
        val showWaitDialog: Boolean = true,
        val waitDialogTitle: String =
            "Please hold on...",
        val waitDialogMessage: String =
            "Submitting your feedback to Slack: ${channel?:username?:webhookUrl} ...",
        val successToastMessage: String =
            "Thank you for your feedback! We will get back to you as soon as possible.",
        val failureToastMessage: String =
            "An error happened - please try again later"
): Listener, AnkoLogger {

    override fun onDismiss() {
        debug {"onDismiss"}
        context.longToast("Dismissed")
    }

    override fun onSendButtonClicked(feedback: Feedback?): Boolean {
        debug {"onSendButtonClicked: webhook='$webhookUrl', channel='$channel', username='$username'"}

        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        val isConnected = activeNetworkInfo ?.isConnectedOrConnecting ?: false
        if (!isConnected) {
            context.longToast("An Internet connection is required to send your feedback on Slack")
            return false
        }

        val progressDialog =
                if (showWaitDialog)
                    context.indeterminateProgressDialog(
                            title = waitDialogTitle,
                            message = waitDialogMessage)
                else null
        progressDialog?.show()

        val deviceAndAppInfo = feedback
                ?.deviceAndAppInfoAsHumanReadableMap
                ?.filter { (_, value) -> value != null }
                ?.map { (key,value) -> "- $key : $value" }
                ?.joinToString (separator = "\n")
                ?: ""
        val feedbackMessage = feedback ?.userComment ?: ""

        doAsync {
            val text = "New Feedback on ${feedback?.appInfo?.applicationId?:"Unknown"}" +
                    "\n\n$feedbackMessage"

            val bodyMap : MutableMap<String, Any> = mutableMapOf(
                    "text" to text
            )
            channel?.let { bodyMap["channel"] = it }
            username?.let { bodyMap["username"] = it }
            iconUrl?.let { bodyMap["icon_url"] = it }
            emojiIcon?.let { bodyMap["emoji_icon"] = it }

            val attachments : MutableList<Map<String, Any>> = mutableListOf()
            val attachment: MutableMap<String, Any> = mutableMapOf(
                    "fallback" to deviceAndAppInfo,
                    "pretext" to deviceAndAppInfo,
                    "color" to "#D00000"
            )
            val attachmentFields : MutableList<Map<String, Any>> = mutableListOf()

            //Context
            val appExecutionContext = mapOf(
                    "title" to "Context",
                    "value" to deviceAndAppInfo,
                    "short" to false
            )
            attachmentFields.add(appExecutionContext)

            //App logs
            if (feedback?.includeLogs == true) {
                val appLogs = mapOf(
                        "title" to "Application Logs",
                        "value" to feedback.logsFile!!.readText(Charset.defaultCharset()),
                        "short" to true
                )
                attachmentFields.add(appLogs)
            }

            //TODO Caveat: App Screenshot cannot be uploaded for now

            attachment["fields"] = attachmentFields
            attachments.add(attachment)
            bodyMap["attachments"] = attachments

            if (debug) {
                Log.d(LOG_TAG, "bodyMap: $bodyMap")
            }

            val response = post(
                    url = webhookUrl,
                    headers = mapOf(
                            "User-Agent" to USER_AGENT,
                            "Content-Type" to APPLICATION_JSON),
                    json = bodyMap)
            val statusCode = response.statusCode
            val responseBody = response.text
            if (debug) {
                debug {">>> POST $webhookUrl"}
                debug {"<<< [$statusCode] POST $webhookUrl: \n$responseBody"}
            }
            uiThread { 
                progressDialog?.cancel()
                when (statusCode) {
                    in 100..399 -> context.longToast(successToastMessage)
                    else -> {
                        debug {"responseBody = $responseBody"}
                        context.longToast("[$statusCode] $failureToastMessage : $responseBody")
                    }
                }
            }
        }

        return true
    }
}