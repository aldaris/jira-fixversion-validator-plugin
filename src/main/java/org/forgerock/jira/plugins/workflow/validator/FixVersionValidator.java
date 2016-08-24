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
 * Copyright 2015-2016 ForgeRock AS.
 */
package org.forgerock.jira.plugins.workflow.validator;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.workflow.WorkflowActionsBean;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

@JiraComponent
public class FixVersionValidator implements Validator {

    private final ValidatorUtils validatorUtils;
    private final I18nHelper i18n;
    private final WorkflowActionsBean workflowActionsBean = new WorkflowActionsBean();

    @Inject
    public FixVersionValidator(ValidatorUtils validatorUtils, @ComponentImport I18nHelper i18n) {
        this.validatorUtils = validatorUtils;
        this.i18n = i18n;
    }

    @Override
    public void validate(Map transientVars, Map args, PropertySet propertySet)
            throws InvalidInputException, WorkflowException {
        Issue issue = (Issue) transientVars.get("issue");
        List<Resolution> protectedResolutions = validatorUtils.getResolutions(args);

        if (issue.getIssueType().isSubTask()) {
            //ignore subtasks
            return;
        }

        if (protectedResolutions.contains(issue.getResolution())) {
            if (issue.getFixVersions().isEmpty()) {
                throw getException(transientVars, IssueFieldConstants.FIX_FOR_VERSIONS,
                        "fixversion-validator.missing.fixversion");
            }
        } else if (!issue.getFixVersions().isEmpty()) {
            throw getException(transientVars, IssueFieldConstants.RESOLUTION,
                    "fixversion-validator.incorrect.resolution");
        }
    }

    private InvalidInputException getException(Map transientVars, String field, String key) {
        InvalidInputException invalidInputException = new InvalidInputException();

        if (isFieldOnScreen(transientVars, field)) {
            invalidInputException.addError(field, i18n.getText(key));
        } else {
            invalidInputException.addError(i18n.getText(key));
        }
        return invalidInputException;
    }

    private boolean isFieldOnScreen(Map transientVars, String field) {
        if (transientVars.containsKey("descriptor") && transientVars.containsKey("actionId")) {
            WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor) transientVars.get("descriptor");
            Integer actionId = (Integer) transientVars.get("actionId");
            ActionDescriptor actionDescriptor = workflowDescriptor.getAction(actionId.intValue());
            FieldScreen fieldScreen = workflowActionsBean.getFieldScreenForView(actionDescriptor);
            if (fieldScreen != null) {
                for (FieldScreenTab tab : fieldScreen.getTabs()) {
                    for (FieldScreenLayoutItem fieldScreenLayoutItem : tab.getFieldScreenLayoutItems()) {
                        if (field.equals(fieldScreenLayoutItem.getFieldId())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
