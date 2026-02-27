package org.openmrs.module.metadatatools.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatatools.analyzer.AnalysisReport;
import org.openmrs.module.metadatatools.analyzer.ConceptAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConceptAnalyzerRestController {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    ConceptAnalyzer conceptAnalyzer;

    @Autowired
    ConceptService conceptService;

    @GetMapping(value = "/rest/v1/metadatatools/analyzer/concept/{reference}")
    public ResponseEntity<Object> getConceptAnalysis(
            @PathVariable("reference") String reference,
            @RequestParam(value = "includeReferencesWithNoData", required = false, defaultValue = "false") boolean includeReferencesWithNoData
    ) {
        if (!Context.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        try {
            Concept concept = conceptService.getConceptByReference(reference);
            if (concept == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
            AnalysisReport analysisReport = conceptAnalyzer.analyzeConcept(concept, includeReferencesWithNoData);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/json;charset=UTF-8"))
                    .body(toJson(analysisReport));
        }
        catch (Exception e) {
            log.error("Error generating initializer export", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private String toJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
