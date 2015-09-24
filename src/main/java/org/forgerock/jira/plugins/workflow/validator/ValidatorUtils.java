/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */
package org.forgerock.jira.plugins.workflow.validator;

import com.atlassian.jira.config.ResolutionManager;
import com.atlassian.jira.issue.resolution.Resolution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ValidatorUtils {

    public static final String AVAILABLE_RESOLUTIONS = "availableResolutions";
    public static final String SUBMITTED_RESOLUTIONS = "submittedResolutions";
    public static final String SELECTED_RESOLUTIONS = "selectedResolutions";
    private final ResolutionManager resolutionManager;

    public ValidatorUtils(ResolutionManager resolutionManager) {
        this.resolutionManager = resolutionManager;
    }

    public List<Resolution> getResolutions(Map<String, ?> args) {
        List<Resolution> ret = new ArrayList<Resolution>();
        String resolutions = (String) args.get(SUBMITTED_RESOLUTIONS);
        if (resolutions != null) {
            for (String resolution : resolutions.split("\\|")) {
                ret.add(resolutionManager.getResolutionByName(resolution));
            }
        }

        Collections.sort(ret, new ResolutionComparator());
        return ret;
    }

    public List<Resolution> getAllResolutions() {
        return resolutionManager.getResolutions();
    }

    public String resolutionsAsString(List<Resolution> resolutions) {
        StringBuilder sb = new StringBuilder();
        for (Resolution resolution : resolutions) {
            sb.append(resolution.getName()).append("|");
        }
        return sb.toString();
    }

    private static class ResolutionComparator implements Comparator<Resolution> {

        @Override
        public int compare(Resolution o1, Resolution o2) {
            return o1.getId().compareTo(o2.getId());
        }

    }
}
