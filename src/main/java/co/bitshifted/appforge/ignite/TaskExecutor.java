/*
 *
 *  * Copyright (c) 2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

    private static final TaskExecutor INSTANCE;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int QUEUE_CAPACITY = 10;

    static {
        INSTANCE = new TaskExecutor();
    }

    private final ThreadPoolExecutor executor;

    private TaskExecutor() {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        this.executor = new ThreadPoolExecutor(cpuCount, 2 * cpuCount, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_CAPACITY));
    }

    public static TaskExecutor getInstance() {
        return INSTANCE;
    }

    public  Future<?> start(Task worker) {
        return executor.submit(worker);
    }

    public void stop() {
        executor.shutdown();
    }

}
