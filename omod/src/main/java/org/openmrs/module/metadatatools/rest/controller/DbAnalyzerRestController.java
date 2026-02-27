package org.openmrs.module.metadatatools.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatatools.analyzer.db.ForeignKeyReference;
import org.openmrs.module.metadatatools.analyzer.db.MysqlAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DbAnalyzerRestController {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    MysqlAnalyzer mysqlAnalyzer;

    @GetMapping(value = "/rest/v1/metadatatools/analyzer/db/{foreignkeys}/{table}")
    public ResponseEntity<Object> export(@PathVariable("table") String table) {

        if (!Context.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        try {
            List<ForeignKeyReference> foreignKeyReferences = mysqlAnalyzer.getForeignKeyReferencesToTable(table);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/json;charset=UTF-8"))
                    .body(toJson(foreignKeyReferences));
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
