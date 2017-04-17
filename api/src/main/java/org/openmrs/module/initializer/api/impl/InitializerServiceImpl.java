/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigLoaderUtil;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesConfig;
import org.openmrs.util.OpenmrsUtil;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public String getConfigPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.CONFIG_PATH).toString();
	}
	
	@Override
	public String getAddressHierarchyConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_ADDR)
		        .toString();
	}
	
	@Override
	public String getGlobalPropertiesConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_GP)
		        .toString();
	}
	
	@Override
	public void loadGlobalProperties() {
		
		final ConfigLoaderUtil util = new ConfigLoaderUtil(getGlobalPropertiesConfigPath()); // a config. loader util for the target dir
		
		final List<GlobalProperty> globalProperties = new ArrayList<GlobalProperty>();
		for (File file : util.getFiles("xml")) { // processing all the XML files inside the domain
		
			String fileRelPath = util.getRelativePath(file.getPath());
			String checksum = util.getChecksumIfChanged(fileRelPath);
			if (checksum.isEmpty()) {
				continue;
			}
			GlobalPropertiesConfig config = new GlobalPropertiesConfig();
			try {
				config = InitializerSerializer.getGlobalPropertiesConfig(new FileInputStream(file));
				globalProperties.addAll(config.getGlobalProperties());
				util.writeChecksum(fileRelPath, checksum); // the updated config. file is marked as processed
			}
			catch (Exception e) {
				log.error("Could not load the global properties from file: " + file.getPath());
			}
		}
		
		Context.getAdministrationService().saveGlobalProperties(globalProperties);
	}
}
