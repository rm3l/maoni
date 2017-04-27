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
const val USER_AGENT: String = "maoni-github (v2.4.0-alpha3)"
const val APPLICATION_JSON: String = "application/json"

open class MaoniGithubListener(
        val context: Context,
        val githubUsername: String,
        val githubPersonalAccessToken: String,
        val githubRepoOwner: String,
        val githubRepo: String,
        val debug: Boolean = false,
        val waitDialogTitle: String = "Please hold on...",
        val waitDialogMessage: String = "Submitting your feedback to Github repo: $githubRepoOwner/$githubRepo ...",
        githubIssueTitlePrefix: String? = "Maoni",
        githubIssueBodyPrefix: String? = null,
        githubIssueBodySuffix: String? = null,
        val githubIssueLabels: Array<String>? = null,
        val githubIssueAssignees: Array<String>? = null,
        val successToastMessage: String = "Thank you for your feedback!",
        val failureToastMessage: String = "An error happened - please try again later"
) : Listener {

    private val ghIssueUrl: String =
            "https://api.github.com/repos/%s/%s/issues".format(githubRepoOwner, githubRepo)
    private val ghIssueTitlePrefix: String =
            if (githubIssueTitlePrefix != null) "[$githubIssueTitlePrefix] " else ""
    private val ghIssueBodyPrefix: String =
            if (githubIssueBodyPrefix != null) "$githubIssueBodyPrefix\n" else ""
    private val ghIssueBodySuffix: String =
            if (githubIssueBodySuffix != null) "$githubIssueBodySuffix\n" else ""

    override fun onSendButtonClicked(feedback: Feedback?): Boolean {
        AnkoLogger(context).debug("onSendButtonClicked")
        val progressDialog = context.indeterminateProgressDialog(title = waitDialogTitle, message = waitDialogMessage)
        progressDialog.show()
        val feedbackMessage = feedback
                ?.deviceAndAppInfoAsHumanReadableMap
                ?.filter { (_, value) -> value != null }
                ?.map { (key,value) -> "- $key : $value" }
                ?.joinToString (separator = "\n")
                ?: ""
        doAsync {
            val response = post(
                    url = ghIssueUrl,
                    headers = mapOf(
                            "User-Agent" to USER_AGENT,
                            "Content-Type" to APPLICATION_JSON,
                            "Accept" to APPLICATION_JSON),
                    auth = AndroidBasicAuthorization(githubUsername, githubPersonalAccessToken),
                    data = mapOf(
                            "title" to "${ghIssueTitlePrefix}New Feedback",
                            "body" to "${ghIssueBodyPrefix}${feedbackMessage}\n${ghIssueBodySuffix}",
                            "labels" to (githubIssueLabels ?: emptyArray<String>()),
                            "assignees" to (githubIssueAssignees ?: emptyArray<String>())))
            val statusCode = response.statusCode
            val responseBody = response.jsonObject
            if (debug) {
                AnkoLogger(context).debug(">>> POST $ghIssueUrl")
                AnkoLogger(context).debug("<<< [$statusCode] POST $ghIssueUrl: \n$responseBody")
            }
            uiThread {
                progressDialog.cancel()
                when (statusCode) {
                    in 100..399 -> context.longToast(successToastMessage)
                    else -> context.longToast("[$statusCode] $failureToastMessage")
                }
            }
        }

        return true
    }

    override fun onDismiss() {
        AnkoLogger(context).debug("onDismiss")
        context.longToast("Dismissed")
    }

}