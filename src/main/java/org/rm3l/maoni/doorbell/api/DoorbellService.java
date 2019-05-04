/*
 * Copyright (c) 2016 Armel Soro
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
package org.rm3l.maoni.doorbell.api;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DoorbellService {

    String MULTIPART_FORM_DATA = "multipart/form-data";

    @POST("applications/{id}/open")
    Call<ResponseBody> openApplication(
            @HeaderMap final Map<String, String> httpHeaders,
            @Path("id") final int applicationId,
            @Query("key") final String key);

    @POST("applications/{id}/submit")
    Call<ResponseBody> submitFeedbackForm(
            @HeaderMap final Map<String, String> httpHeaders,
            @Path("id") final int applicationId,
            @Query("key") final String key,
            @Body final DoorbellSubmitRequest request);

    @Multipart
    @POST("applications/{id}/upload")
    Call<String[]> uploadScreenshot(
            @HeaderMap final Map<String, String> httpHeaders,
            @Path("id") final int applicationId,
            @Query("key") final String key,
            @Part("files[]\"; filename=\"screenshot.png\" ") final RequestBody filename);

    @Multipart
    @POST("applications/{id}/upload")
    Call<String[]> uploadLogs(
            @HeaderMap final Map<String, String> httpHeaders,
            @Path("id") final int applicationId,
            @Query("key") final String key,
            @Part("files[]\"; filename=\"logs.txt\" ") final RequestBody filename);
}
