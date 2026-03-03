package org.openmrs.module.metadatatools.analyzer;

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.module.metadatatools.analyzer.db.ForeignKeyReference;
import org.openmrs.module.metadatatools.analyzer.db.ForeignKeyReferenceCount;
import org.openmrs.module.metadatatools.analyzer.db.MysqlAnalyzer;
import org.openmrs.module.metadatatools.analyzer.db.TableColumn;
import org.openmrs.module.metadatatools.analyzer.db.TextReference;
import org.openmrs.module.metadatatools.analyzer.db.TextReferenceCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConceptAnalyzer {

    private Logger log = LoggerFactory.getLogger(ConceptAnalyzer.class);

    @Autowired
    MysqlAnalyzer mysqlAnalyzer;

    public AnalysisReport analyzeConcept(Concept concept, boolean includeReferencesWithNoData) {
        log.debug("Getting AnalysisReport for concept " + concept);
        AnalysisReport analysisReport = new AnalysisReport();
        analysisReport.setTable("concept");
        analysisReport.setId(concept.getConceptId());
        analysisReport.setUuid(concept.getUuid());
        analysisReport.setDisplay(concept.getDisplayString());
        analysisReport.setForeignKeyReferences(new ArrayList<>());
        log.debug("Getting foreign key references");
        for (ForeignKeyReference r : getForeignKeyReferences()) {
            long num = mysqlAnalyzer.getNumberOfReferences(r, concept.getConceptId());
            log.debug(r + ": " + num);
            if (num > 0 || includeReferencesWithNoData) {
                analysisReport.getForeignKeyReferences().add(new ForeignKeyReferenceCount(r, num));
            }
        }
        log.debug("Getting text references");
        analysisReport.setTextReferences(new ArrayList<>());
        List<String> referenceCodes = getConceptReferenceCodes(concept);
        for (TableColumn tc : mysqlAnalyzer.getTextColumnsToCheckForReferences()) {
            for (String reference : referenceCodes) {
                TextReference textReference = new TextReference(tc, reference);
                long num = mysqlAnalyzer.getNumberOfTextReferences(tc, reference);
                log.debug(tc + ": " + reference + ": " + num);
                if (num > 0 || includeReferencesWithNoData) {
                    analysisReport.getTextReferences().add(new TextReferenceCount(textReference, num));
                }
            }
        }
        log.debug("Getting conceptId references in htmlforms");
        TableColumn tc = new TableColumn("htmlformentry_html_form", "xml_data");
        String reference = concept.getConceptId().toString();
        TextReference textReference = new TextReference(tc, reference);
        long num = mysqlAnalyzer.getNumberOfTextReferences(tc, reference);
        if (num > 0 || includeReferencesWithNoData) {
            analysisReport.getTextReferences().add(new TextReferenceCount(textReference, num));
        }
        return analysisReport;
    }

    public List<ForeignKeyReference> getForeignKeyReferences() {
        return mysqlAnalyzer.getForeignKeyReferencesToTable("concept");
    }

    public List<String> getConceptReferenceCodes(Concept concept) {
        List<String> ret = new ArrayList<>();
        ret.add(concept.getUuid());
        for (ConceptMap cm: concept.getConceptMappings()) {
            String source = cm.getConceptReferenceTerm().getConceptSource().getName();
            String term = cm.getConceptReferenceTerm().getCode();
            ret.add(source+":"+term);
        }
        return ret;
    }
}
