package com.github.jmfayard.okandroid.jobs

import com.evernote.android.job.Job
import com.github.jmfayard.room.DB
import timber.log.Timber

class SyncJob: Job() {
    override fun onRunJob(params: Params): Result {
        Timber.i("SyncJob ($params)")
        try {
            val data = syncPersons()
            Timber.i("SyncJob SUCCESS: $data")
            return Result.SUCCESS
        } catch (e: Exception) {
            Timber.e(e, "Sync failed with $e")
        }
        return Result.FAILURE
    }

    fun syncPersons() {
        val persons = DB.persons().unsyncedPeople()
        for (p in persons) {
            Timber.w("Syncing $p")
            DB.persons().markAsSynced(p.uid, true)
        }
    }


}