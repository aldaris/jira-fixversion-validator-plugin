# JIRA fixVersion validator plugin

This plugin allows JIRA administrators to perform additional validations on the fixVersion field during Workflow
Transitions. The plugin allows selecting a set of "protected" Issue Resolution types, for which the plugin will enforce
the presence of the fixVersion field. For all the other Resolution types the plugin will enforce that the fixVersion
field is left empty.

For example:
* When Resolution is Fixed, fixVersion MUST be set.
* When Resolution is Won't Fix, fixVersion MUST NOT be set.

## Configuration

* Open up the workflow where you want to add this new validation
* Select the relevant State transition
* Click on the Validators tab
* Add `Fix Version Validator` type of Validator
* On the Validator's configuration page select which Resolutions MUST have fixVersions
* Publish your draft workflow to roll out your changes

## Compile

To compile this JIRA plugin, one has to first install the [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project).

Once the SDK is installed, testing the plugin should be a matter of:

    atlas-run

To debug the JIRA plugin:

    atlas-debug

## Licence

This project is based on the [JIRA Suite Utilities](https://github.com/atlassian/jira-suite-utilities) project, hence the corresponding `licence.txt` applies to this project as well. The changes made are licensed under the ForgeRock CDDL license: http://forgerock.org/license/CDDLv1.0.html
