package org.openmrs.module.metadatatools.analyzer;

import lombok.Data;
import org.openmrs.module.metadatatools.analyzer.db.ForeignKeyReferenceCount;
import org.openmrs.module.metadatatools.analyzer.db.TextReferenceCount;

import java.util.List;

@Data
public class AnalysisReport {

    private String table;
    private Integer id;
    private String uuid;
    private String display;
    List<ForeignKeyReferenceCount> foreignKeyReferences;
    List<TextReferenceCount> textReferences;
}
