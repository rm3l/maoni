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
package org.rm3l.maoni.jira

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import khttp.extensions.fileLike
import khttp.post
import khttp.structures.files.FileLike
import org.jetbrains.anko.*
import org.json.JSONArray
import org.rm3l.maoni.common.contract.Listener
import org.rm3l.maoni.common.model.Feedback
import org.rm3l.maoni.jira.android.AndroidBasicAuthorization

/**
 * Callback for Maoni that takes care of sending the Feedback as a Jira issue for the specified repo.
 * <p>
 * Written in Kotlin for conciseness
 */
const val USER_AGENT = "maoni-jira (v8.0.2)"
const val APPLICATION_JSON = "application/json"
const val ISSUE_SUMMARY_MAX_LINES = 50

@Suppress("unused")
open class MaoniJiraListener @JvmOverloads constructor(
        val context: Context,
        val debug: Boolean = false,

        val jiraServerRestApiBaseUrl: String,
        val jiraReporterUsername: String,
        val jiraReporterPassword: String,

        val jiraProjectKey: String,
        val jiraIssueSummaryPrefix: String? = "Maoni",
        val jiraIssueDescriptionPrefix: String? = null,
        val jiraIssueDescriptionSuffix: String? = null,
        val jiraIssueType: String,
        val jiraIssueCustomFieldsMap: Map<String, String>? = null,

        val waitDialogTitle: String = "Please hold on...",
        val waitDialogMessage: String = "Submitting your feedback to JIRA Project: $jiraProjectKey ...",
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

        val jiraIssueUrl = "$jiraServerRestApiBaseUrl/issue"

        val jiraIssueSummaryPrefix =
                if (jiraIssueSummaryPrefix != null) "[$jiraIssueSummaryPrefix] " else ""
        val jiraIssueDescriptionPrefix =
                if (jiraIssueDescriptionPrefix != null) "$jiraIssueDescriptionPrefix\n" else ""
        val jiraIssueDescriptionSuffix =
                if (jiraIssueDescriptionSuffix != null) "$jiraIssueDescriptionSuffix\n" else ""

        val progressDialog = context.indeterminateProgressDialog(title = waitDialogTitle, message = waitDialogMessage)
        progressDialog.show()

        val deviceAndAppInfo = feedback
                ?.deviceAndAppInfoAsHumanReadableMap
                ?.filter { (_, value) -> value != null }
                ?.map { (key,value) -> "- $key : $value" }
                ?.joinToString (separator = "\n")
                ?: ""
        val feedbackMessage = feedback ?.userComment ?: ""
        val feedbackMessageLines = feedbackMessage.split(System.lineSeparator())
        val firstLineOfMessage = if (feedbackMessageLines.isEmpty()) "" else feedbackMessageLines[0]
        val summary: String
        if (firstLineOfMessage.length >= ISSUE_SUMMARY_MAX_LINES) {
            summary = (firstLineOfMessage.substring(0, ISSUE_SUMMARY_MAX_LINES) + "...")
        } else {
            summary = firstLineOfMessage
        }

        val fieldsMap = mutableMapOf(
                "project" to mapOf("key" to jiraProjectKey),
                "summary" to "$jiraIssueSummaryPrefix$summary",
                "description" to jiraIssueDescriptionPrefix +
                        "$feedbackMessage" +
                        "\n$jiraIssueDescriptionSuffix" +
                        "\n\n**Context**" +
                        "\n$deviceAndAppInfo",
                "issuetype" to mapOf("name" to jiraIssueType))
        fieldsMap.putAll(jiraIssueCustomFieldsMap?: emptyMap())

        val auth = AndroidBasicAuthorization(jiraReporterUsername, jiraReporterPassword)

        doAsync {
            val issueCreationResponse = post(
                    url = jiraIssueUrl,
                    headers = mapOf(
                            "User-Agent" to USER_AGENT,
                            "Content-Type" to APPLICATION_JSON,
                            "Accept" to APPLICATION_JSON),
                    auth = auth,
                    json = mapOf("fields" to fieldsMap)
            )
            val statusCode = issueCreationResponse.statusCode
            val responseBody = issueCreationResponse.jsonObject
            if (debug) {
                debug {">>> POST $jiraIssueUrl"}
                debug {"<<< [$statusCode] POST $jiraIssueUrl: \n$responseBody"}
            }
            uiThread {
                when (statusCode) {
                    in 100..399 -> {
                        //Upload attachments, if any
                        doAsync {

                            val issueKey = responseBody.getString("key")
                            val issueAttachmentUrl = "$jiraIssueUrl/$issueKey/attachments"

                            val listOfFiles = mutableListOf<FileLike>()
                            if (feedback != null) {
                                if (feedback.includeScreenshot) {
                                    listOfFiles.add(feedback.screenshotFile.fileLike())
                                }
                                if (feedback.includeLogs) {
                                    listOfFiles.add(feedback.logsFile.fileLike())
                                }
                            }
                            val attachmentsUploadResponseStatusCode: Int
                            val attachmentsUploadResponseResponseBody: JSONArray?
                            val doSendAttachmentsRequest = !listOfFiles.isEmpty()
                            if (doSendAttachmentsRequest) {
                                val attachmentsUploadResponse = post(
                                        url = issueAttachmentUrl,
                                        headers = mapOf("X-Atlassian-Token" to "nocheck"),
                                        auth = auth,
                                        files = listOfFiles
                                )
                                attachmentsUploadResponseStatusCode = attachmentsUploadResponse.statusCode
                                attachmentsUploadResponseResponseBody = attachmentsUploadResponse.jsonArray
                                if (debug) {
                                    debug {">>> POST $issueAttachmentUrl"}
                                    debug {"<<< [$attachmentsUploadResponseStatusCode] " +
                                            "POST $issueAttachmentUrl: \n$attachmentsUploadResponseResponseBody"}
                                }
                            } else {
                                attachmentsUploadResponseStatusCode = 204
                                attachmentsUploadResponseResponseBody = null
                            }

                            uiThread {
                                progressDialog.cancel()
                                if (doSendAttachmentsRequest) {
                                    when (attachmentsUploadResponseStatusCode) {
                                        in 100..399 -> {
                                            context.longToast("$successToastMessage. Issue created: $issueKey")
                                        }
                                        else -> {
                                            debug { "responseBody = $attachmentsUploadResponseResponseBody" }
                                            context.longToast(
                                                    "$successToastMessage. Issue created: $issueKey, but could not upload attachments: " +
                                                            "[$attachmentsUploadResponseStatusCode] $attachmentsUploadResponseResponseBody")
                                        }
                                    }
                                } else {
                                    context.longToast("$successToastMessage. Issue created: $issueKey")
                                }
                            }
                        }
                    }
                    else -> {
                        progressDialog.cancel()
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
    }

}
