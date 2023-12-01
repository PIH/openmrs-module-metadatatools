/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.metadatatools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class SaveConceptAdvice implements AfterReturningAdvice {

	protected final Log log = LogFactory.getLog(getClass());

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
		if (method.getName().equals("saveConcept")) {
			if ("true".equals(Context.getRuntimeProperties().getProperty("automaticallyCreatePihMapping"))) {
				Concept concept = (Concept) args[0];
				ConceptService conceptService = Context.getConceptService();
				String conceptId = concept.getConceptId().toString();
				ConceptSource pihSource = conceptService.getConceptSourceByName("PIH");
				ConceptMapType sameAsType = conceptService.getConceptMapTypeByName("SAME-AS");
				boolean found = false;
				for (ConceptMap conceptMap : concept.getConceptMappings()) {
					if (conceptMap.getConceptMapType().equals(sameAsType)) {
						if (conceptMap.getConceptReferenceTerm().getConceptSource().equals(pihSource)) {
							if (conceptMap.getConceptReferenceTerm().getCode().equals(conceptId)) {
								found = true;
							}
						}
					}
				}
				if (!found) {
					ConceptReferenceTerm crt = conceptService.getConceptReferenceTermByCode(conceptId, pihSource);
					if (crt != null) {
						log.warn("Reusing existing term for PIH:" + conceptId + ". Term = " + crt.getId());
					} else {
						log.warn("Creating a new term for PIH:" + conceptId + " mapping");
						crt = new ConceptReferenceTerm(pihSource, conceptId, "");
						crt = conceptService.saveConceptReferenceTerm(crt);
					}
					log.warn("Adding term to concept");
					ConceptMap conceptMap = new ConceptMap();
					conceptMap.setConcept(concept);
					conceptMap.setConceptMapType(sameAsType);
					conceptMap.setConceptReferenceTerm(crt);
					concept.addConceptMapping(conceptMap);
					conceptService.saveConcept(concept);
					log.warn("Successfully added PIH:" + conceptId + " mapping");
				} else {
					log.warn("Concept " + conceptId + " already has a PIH:" + conceptId + " mapping");
				}
			}
		}
	}
}
