package com.github.jmfayard.jobs

import com.evernote.android.job.Job
import okhttp3.*
import timber.log.Timber


private val URL = "https://httpbin.org/post"

class HttpbinJob : Job() {


    override fun onRunJob(params: Params): Result {
        Timber.e("RequestbinJob: $params")

        try {
            val response = sendRequestbinRequest()
            Timber.i("Response: $response")
            if (response.isSuccessful) {
                return Result.SUCCESS
            }
        } catch (e: Exception) {
            Timber.e(e, "Request failed with $e")
        }
        return Result.RESCHEDULE
    }


    fun sendRequestbinRequest(): Response {

        val mediaType = MediaType.parse("text/plain")
        val body = RequestBody.create(mediaType, "Saluton Mondo, kiel vi?")
        val request = Request.Builder()
                .url("$URL?lang=esperanto")
                .post(body)
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "43915c21-fde9-32db-f803-2c5b72ec69d1")
                .build()

        return Jobs.client.newCall(request).execute()
    }
}