package org.openmrs.module.metadatatools.rest.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatatools.initializer.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

@RestController
public class InitializerExportRestController {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    List<Exporter> exporters;

    @GetMapping(value = "/rest/v1/metadatatools/initializer/export")
    public ResponseEntity<byte[]> export(@RequestHeader HttpHeaders headers) {

        if (!Context.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(bos)) {
                for (Exporter exporter : exporters) {
                    exporter.export(zos);
                }
            }
            byte[] export =  bos.toByteArray();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header("Content-Disposition", "attachment; filename=\"initializer-export.zip\"")
                    .body(export);
        }
        catch (Exception e) {
            log.error("Error generating initializer export", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }
}
