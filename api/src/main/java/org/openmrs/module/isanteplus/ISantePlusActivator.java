/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.isanteplus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emrapi.utils.MetadataUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.isanteplus.api.IsantePlusService;
import org.openmrs.module.isanteplus.deploy.bundle.IsantePlusMetadataBundle;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.ui.framework.resource.ResourceFactory;

/**
 * This class contains the logic that is run every time this module is either
 * started or stopped.
 */
public class ISantePlusActivator implements ModuleActivator {

	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing iSantePlus Patient Dashboard Module");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("iSantePlus Patient Dashboard Module refreshed");
	}

	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting iSantePlus Patient Dashboard Module");
	}

	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		Context.getService(IsantePlusService.class).toggleRecentVitalsSection(
				new IsantePlusManager().getToogleMostRecentVitalsExtension());
		try {
			loadIsantePlusHtmlForms();
			//installMetadataPackages();
			installMetadataBundles();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("iSantePlus Patient Dashboard Module started");
	}

	/**
	 * Load in the iSantePlus htmlforms that are defiend in the IsantePlusConstants.ISANTEPLUS_FORMFILE_NAMES
	 * Uses the HtmlFormEntry module
	 * @throws Exception
	 */
	private void loadIsantePlusHtmlForms() throws Exception {
		try {
			ResourceFactory resourceFactory = ResourceFactory.getInstance();
			FormService formService = Context.getFormService();

			for (String fileName : IsantePlusConstants.ISANTEPLUS_FORMFILE_NAMES) {
				HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService,
						Context.getService(HtmlFormEntryService.class),
						"isanteplus:htmlforms/" + fileName);
			}
		} catch (Exception e) {
			if (ResourceFactory.getInstance().getResourceProviders() == null) {
				log.error("Unable to load HTML forms--this error is expected when running component tests");
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Install the metadata packages using the MetadataSharing Module
	 * @throws Exception
	 */
	private void installMetadataPackages() throws Exception {

        MetadataUtil.setupSpecificMetadata(getClass().getClassLoader(), "PIH_REGISTRATION_CONCEPTS_METADATA_PACKAGE_UUID");

        Context.flushSession();

    }

	/**
	 * Installs metadataBundles from multiple providers all found in the IsantePlusMetadataBundle.class
	 * Uses the MetadataDeploy Module
	 */
    private void installMetadataBundles() {

        MetadataDeployService deployService = Context.getService(MetadataDeployService.class);

        //Deploy metadata packages
        deployService.installBundle(Context.getRegisteredComponents(IsantePlusMetadataBundle.class).get(0));

    }

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		Context.getService(IsantePlusService.class).toggleRecentVitalsSection(null);

		log.info("Stopping iSantePlus Patient Dashboard Module");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("iSantePlus Patient Dashboard Module stopped");
	}

}
