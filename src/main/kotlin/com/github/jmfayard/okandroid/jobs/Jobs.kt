package com.github.jmfayard.okandroid.jobs

import android.app.Application
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.github.jmfayard.okandroid.jobs.Jobs.JobTag.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber


class Jobs : JobCreator {

    override fun create(tag: String): Job? {
        val jobTag = values().firstOrNull { it.name == tag } ?: return null
        return when(jobTag) {
            Sync -> SyncJob()
            RequestBin -> HttpbinJob()
        }
    }

    enum class JobTag {
        Sync, RequestBin
    }

    companion object {
        fun initialize(app: Application) {
            JobManager.create(app).addJobCreator(Jobs())
            val jobManager = JobManager.instance()
            jobManager.allJobRequests
                    .filter { r: JobRequest -> r.isPeriodic }
                    .forEach { r ->
                        Timber.v("Cancelling job $r")
                        jobManager.cancel(r.jobId)
                    }
            Jobs.launchSyncPeriodic()
        }

        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()


        fun create(tag: JobTag): JobRequest.Builder = JobRequest.Builder(tag.name)

        fun postSoonToHttpbin(): Int = create(RequestBin)
                .setExecutionWindow(5_000L,15_000L)
                .setBackoffCriteria(300, JobRequest.BackoffPolicy.EXPONENTIAL)
                .build()
                .schedule()

        fun postNowToHttpbin(): Int = create(RequestBin)
                .setBackoffCriteria(300, JobRequest.BackoffPolicy.EXPONENTIAL)
                .startNow()
                .build()
                .schedule()

        fun launchSyncPeriodic() = create(Sync)
                .setPeriodic(900_000)
                .build()
                .schedule()

        fun launchSyncNow() = create(Sync)
                .setBackoffCriteria(1_000, JobRequest.BackoffPolicy.EXPONENTIAL)
                .startNow()
                .build()
                .schedule()
    }



}