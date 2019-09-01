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
package org.rm3l.maoni.github

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import khttp.post
import org.jetbrains.anko.*
import org.rm3l.maoni.common.contract.Listener
import org.rm3l.maoni.common.model.Feedback
import org.rm3l.maoni.github.android.AndroidBasicAuthorization

/**
 * Callback for Maoni that takes care of sending the Feedback as a Github issue for the specified repo.
 * <p>
 * Written in Kotlin for conciseness
 */
const val USER_AGENT = "maoni-github (v8.0.2)"
const val APPLICATION_JSON = "application/json"
const val TITLE_MAX_LINES = 50

open class MaoniGithubListener @JvmOverloads constructor(
        val context: Context,
        val githubUsername: String,
        val githubPersonalAccessToken: String,
        val githubRepoOwner: String,
        val githubRepo: String,
        val debug: Boolean = false,
        val waitDialogTitle: String = "Please hold on...",
        val waitDialogMessage: String = "Submitting your feedback to Github repo: $githubRepoOwner/$githubRepo ...",
        val githubIssueTitlePrefix: String? = "Maoni",
        val githubIssueBodyPrefix: String? = null,
        val githubIssueBodySuffix: String? = null,
        val githubIssueLabels: Array<String>? = null,
        val githubIssueAssignees: Array<String>? = null,
        val successToastMessage: String = "Thank you for your feedback!",
        val failureToastMessage: String = "An error happened - please try again later"
) : Listener, AnkoLogger {

    override fun onSendButtonClicked(feedback: Feedback?): Boolean {
        debug {"onSendButtonClicked"}

        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected = activeNetworkInfo ?.isConnectedOrConnecting ?: false
        if (!isConnected) {
            context.longToast("An Internet connection is required to send your feedback")
            return false
        }

        val ghIssueUrl =
                "https://api.github.com/repos/%s/%s/issues".format(githubRepoOwner, githubRepo)
        val ghIssueTitlePrefix =
                if (githubIssueTitlePrefix != null) "[$githubIssueTitlePrefix] " else ""
        val ghIssueBodyPrefix =
                if (githubIssueBodyPrefix != null) "$githubIssueBodyPrefix\n" else ""
        val ghIssueBodySuffix =
                if (githubIssueBodySuffix != null) "$githubIssueBodySuffix\n" else ""

        val progressDialog = context.indeterminateProgressDialog(title = waitDialogTitle, message = waitDialogMessage)
        progressDialog.show()

        val deviceAndAppInfo = feedback
                ?.deviceAndAppInfoAsHumanReadableMap
                ?.filter { (_, value) -> value != null }
                ?.map { (key,value) -> "- $key : $value" }
                ?.joinToString (separator = "\n")
                ?: ""
        val feedbackMessage = feedback ?.userComment ?: ""
        doAsync {
            val feedbackMessageLines = feedbackMessage.split(System.lineSeparator())
            val firstLineOfMessage = if (feedbackMessageLines.isEmpty()) "" else feedbackMessageLines[0]
            val title: String
            if (firstLineOfMessage.length >= TITLE_MAX_LINES) {
                title = (firstLineOfMessage.substring(0, TITLE_MAX_LINES) + "...")
            } else {
                title = firstLineOfMessage
            }
            val response = post(
                    url = ghIssueUrl,
                    headers = mapOf(
                            "User-Agent" to USER_AGENT,
                            "Content-Type" to APPLICATION_JSON,
                            "Accept" to APPLICATION_JSON),
                    auth = AndroidBasicAuthorization(githubUsername, githubPersonalAccessToken),
                    json = mapOf(
                            "title" to "$ghIssueTitlePrefix$title",
                            "body" to ghIssueBodyPrefix +
                                    "$feedbackMessage" +
                                    "\n$ghIssueBodySuffix" +
                                    "\n\n**Context**" +
                                    "\n$deviceAndAppInfo",
                            "labels" to (githubIssueLabels ?: emptyArray<String>()),
                            "assignees" to (githubIssueAssignees ?: emptyArray<String>())))
            val statusCode = response.statusCode
            val responseBody = response.jsonObject
            if (debug) {
                debug {">>> POST $ghIssueUrl"}
                debug {"<<< [$statusCode] POST $ghIssueUrl: \n$responseBody"}
            }
            uiThread {
                progressDialog.cancel()
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

    override fun onDismiss() {
        debug {"onDismiss"}
        context.longToast("Dismissed")
    }

}