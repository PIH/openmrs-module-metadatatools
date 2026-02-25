/*
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

package org.openmrs.module.metadatatools.initializer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.openmrs.module.metadatatools.BaseMetadataToolsTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.ZipOutputStream;

/**
 * Concept Exporter Test
 */
public class ConceptExporterTest extends BaseMetadataToolsTest {

	@Autowired
	ConceptExporter conceptExporter;

	@Test
	public void performTest() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(bos)) {
			conceptExporter.export(zos);
		}
		byte[] export =  bos.toByteArray();
		FileUtils.writeByteArrayToFile(new File("/tmp/concepts.zip"), export);
	}

}
