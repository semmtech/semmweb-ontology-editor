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

package com.semmtech.plugin.semmweb.core.reasoner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;


public class RuleConfiguration {
    private List<String> ruleUrls;

    public RuleConfiguration() {
        this.ruleUrls = Lists.newArrayList();
    }

    public RuleConfiguration(String[] urls) {
        this.ruleUrls = new ArrayList<>(Arrays.asList(urls));
    }

    public List<String> getRuleURLs() {
        return ruleUrls;
    }

    public void addRuleURL(String url) {
        ruleUrls.add(url);
    }

    public void removeRuleURL(String url) {
        ruleUrls.remove(url);
    }
}
