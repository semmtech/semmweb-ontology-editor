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


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.semmtech.TimeTester;


public class QueryBenchmark {

    private static final int TEST_NUM = 1;
    private Query query;
    private TimeTester time;
    private String name;
    private List<DatasetProvider> input;
    private Map<DatasetProvider, QueryBenchmarkResult> results;

    public QueryBenchmark(Query query) {
        this("", query);
    }

    public QueryBenchmark(String name, Query query) {
        this.query = query;
        this.name = name;
        time = new TimeTester();
        results = Maps.newLinkedHashMap();
        input = Lists.newArrayList();
    }

    public void setInput(DatasetProvider provider) {
        input = Lists.newArrayList(provider);
    }

    public void setInput(List<DatasetProvider> providers) {
        input = providers;
    }

    public Object excec() {
        Object qRes = null;
        for (int i = 0; i < TEST_NUM; i++) {
            for (DatasetProvider provider : input) {
                qRes = exec(provider);
            }
        }
        return qRes;
    }

    private Object exec(DatasetProvider provider) {
        QueryBenchmarkResult result = results.get(provider);
        if (result == null) {
            result = new QueryBenchmarkResult(provider.getName());
            results.put(provider, result);
        }

        Dataset dataset = provider.getDataset();

        if (provider.getLastLoadTime() != -1) {
            result.addLoadTime(provider.getLastLoadTime());
        }

        QueryExecution exe = QueryExecutionFactory.create(query, dataset);

        time.start();
        Object qRes = execute(exe);
        long queryTime = time.step();
        result.addTime(queryTime);

        QueryExecutionTester resultTester = provider.getTester();
        if (resultTester != null) {
            time.start();
            resultTester.testResult(qRes);
            long testQuery = time.step();
            result.addResultTime(testQuery);
            result.addResult(resultTester.printResult());
        }

        return qRes;
    }

    private Object execute(QueryExecution exe) {
        Query query = exe.getQuery();

        if (query.isSelectType()) {
            return exe.execSelect();
        }
        if (query.isDescribeType()) {
            return exe.execDescribe();
        }
        if (query.isConstructType()) {
            return exe.execConstruct();
        }
        if (query.isAskType()) {
            return exe.execAsk();
        }

        throw new IllegalStateException("Unrecognized state of the query: " + query.getQueryType());
    }

    public String[][] getTableResult(final boolean includeHeader, final int orderByColumn) {
        int offset = includeHeader ? 1 : 0;

        final String[][] table = new String[results.size() + offset][];

        if (includeHeader) {
            table[0] = new String[] { "Model", "avgTime", "maxTime", "minTime", "times", "type",
                    "loadingTime", "testTime", "testRes" };
        }

        int i = offset;
        for (Object key : results.keySet()) {
            QueryBenchmarkResult res = results.get(key);

            table[i] = new String[] { res.getName(), "" + res.getAverageExecutionTime(),
                    "" + res.getMaxExecutionTime(), "" + res.getMinExecutionTime(),
                    "" + res.getExecutionNumber(), getQueryType(query),
                    "" + res.getAverageLoadTime(), "" + res.getAverageResultTime(),
                    "" + res.getResult() };
            i++;
        }

        if (orderByColumn >= 0) {
            Arrays.sort(table, new Comparator<String[]>() {

                @Override
                public int compare(final String[] entry1, final String[] entry2) {
                    if (includeHeader) {
                        if (Arrays.equals(entry1, table[0])) {
                            return -1000;
                        }
                        else if (Arrays.equals(entry2, table[0])) {
                            return 1000;
                        }
                    }

                    String col1 = entry1[orderByColumn];
                    String col2 = entry2[orderByColumn];

                    try {
                        double num1 = Double.parseDouble(col1);
                        double num2 = Double.parseDouble(col2);
                        return (int) (num2 - num1);
                    }
                    catch (NumberFormatException ex) {
                    }

                    return col1.compareToIgnoreCase(col2);
                }
            });
        }

        return table;
    }

    public String formatResult() {
        String formattedResult = "";

        for (final Object[] row : getTableResult(true, 0)) {
            formattedResult += String.format("%20s%10s%10s%10s%10s%10s%15s%15s%20s\n", row);
        }

        return formattedResult;
    }

    public Map<DatasetProvider, QueryBenchmarkResult> getResults() {
        return results;
    }

    public static String getQueryType(Query query) {
        if (query.isSelectType()) {
            return "select";
        }
        if (query.isDescribeType()) {
            return "describe";
        }
        if (query.isConstructType()) {
            return "construct";
        }
        if (query.isAskType()) {
            return "ask";
        }
        return "unknown";
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        int a = 0, b = 0;

        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            if (i % 2 == 0) {
                a = i;
            }
            else if (i % 3 == 0) {
                b = i;
            }
        }
        System.out.println((System.currentTimeMillis() - time) + " " + a + " " + b);
    }
}
