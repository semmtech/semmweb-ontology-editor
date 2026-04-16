/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.jena.performance.sparql;


import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;


public class QueryBenchmarkResult {
    private String name;
    private List<Object> result;
    private List<Long> executionTimes;
    private List<Long> resultTimes;
    private List<Long> loadTimes;

    public QueryBenchmarkResult(String name) {
        this.name = name;
        result = Lists.newArrayList();
        executionTimes = Lists.newArrayList();
        loadTimes = Lists.newArrayList();
        resultTimes = Lists.newArrayList();
    }

    public String getName() {
        return name;
    }

    public void addLoadTime(long loadTime) {
        loadTimes.add(loadTime);
    }

    public List<Object> getResults() {
        return result;
    }

    public String getResult() {
        if (result.isEmpty()) {
            return "";
        }
        return result.get(0).toString();
    }

    public long getAverageLoadTime() {
        // avoid DivisionByZero error
        if (loadTimes.isEmpty()) {
            return -1;
        }

        long sum = 0;

        for (Long time : loadTimes) {
            sum += time;
        }

        return sum / loadTimes.size();
    }

    public long getAverageResultTime() {
        // avoid DivisionByZero error
        if (resultTimes.isEmpty()) {
            return -1;
        }

        long sum = 0;

        for (Long time : resultTimes) {
            sum += time;
        }

        return sum / resultTimes.size();
    }

    public String getFormattedLoadTime() {
        long loadTime = getAverageLoadTime();
        return String.format(
                "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(loadTime),
                TimeUnit.MILLISECONDS.toSeconds(loadTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(loadTime)));
    }

    public void addResultTime(long time) {
        resultTimes.add(time);
    }

    public void addTime(long time) {
        executionTimes.add(time);
    }

    public void addResult(Object result) {
        this.result.add(result);
    }

    public long getMaxExecutionTime() {
        long maxExecutionTime = -1;

        for (Long time : executionTimes) {
            if (maxExecutionTime == -1 || time > maxExecutionTime) {
                maxExecutionTime = time;
            }
        }

        return maxExecutionTime;
    }

    public long getMinExecutionTime() {
        long minExecutionTime = -1;

        for (Long time : executionTimes) {
            if (minExecutionTime == -1 || time < minExecutionTime) {
                minExecutionTime = time;
            }
        }

        return minExecutionTime;
    }

    public long getAverageExecutionTime() {
        // avoid DivisionByZero error
        if (executionTimes.isEmpty()) {
            return -1;
        }

        long sum = 0;

        for (Long time : executionTimes) {
            sum += time;
        }

        return sum / executionTimes.size();
    }

    public int getExecutionNumber() {
        return executionTimes.size();
    }
}