package com.example.homeworkfragments

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Executors {

    private var mDiskIO: Executor
    private var mMainThread: Executor

    private constructor(diskIO: Executor, mainThread: Executor) {
        this.mDiskIO = diskIO
        this.mMainThread = mainThread
    }

    constructor(): this(Executors.newSingleThreadExecutor(), MainThreadExecutor())

    fun diskIO(): Executor {
        return mDiskIO
    }

    fun mainThread(): Executor {
        return mMainThread
    }

    fun shutdown() {
        (mDiskIO as ExecutorService).shutdown()
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(@NonNull command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}