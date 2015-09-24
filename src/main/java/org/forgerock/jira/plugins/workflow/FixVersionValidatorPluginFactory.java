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
package org.forgerock.jira.plugins.workflow;

import static org.forgerock.jira.plugins.workflow.validator.ValidatorUtils.*;

import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.forgerock.jira.plugins.workflow.validator.ValidatorUtils;

public class FixVersionValidatorPluginFactory extends AbstractWorkflowPluginFactory
        implements WorkflowPluginValidatorFactory {

    private final ValidatorUtils validatorUtils;

    public FixVersionValidatorPluginFactory(ValidatorUtils validatorUtils) {
        this.validatorUtils = validatorUtils;
    }

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        velocityParams.put(AVAILABLE_RESOLUTIONS, validatorUtils.getAllResolutions());
    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
        List<Resolution> selected = validatorUtils.getResolutions(validatorDescriptor.getArgs());
        List<Resolution> available = validatorUtils.getAllResolutions();
        available.removeAll(selected);

        velocityParams.put(AVAILABLE_RESOLUTIONS, available);
        velocityParams.put(SELECTED_RESOLUTIONS, selected);
        velocityParams.put(SUBMITTED_RESOLUTIONS, validatorUtils.resolutionsAsString(selected));
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;

        velocityParams.put(SUBMITTED_RESOLUTIONS, validatorUtils.getResolutions(validatorDescriptor.getArgs()));
    }

    @Override
    public Map<String, ?> getDescriptorParams(Map<String, Object> validatorParams) {
        Map<String, String> params = new HashMap<String, String>(0);
        params.put(SUBMITTED_RESOLUTIONS, extractSingleParam(validatorParams, SUBMITTED_RESOLUTIONS));

        return params;
    }
}
