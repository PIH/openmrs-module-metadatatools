package org.openmrs.module.metadatatools.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ConceptBuilderExportController {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    ConceptService conceptService;
    
    public static Map<String, String> getOverrides() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("ConceptDatatype:8d4a4488-c2cc-11de-8d13-0010c6dffd0f", "numeric");
        m.put("ConceptDatatype:8d4a48b6-c2cc-11de-8d13-0010c6dffd0f", "coded");
        m.put("ConceptDatatype:8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f", "text");
        m.put("ConceptDatatype:8d4a5af4-c2cc-11de-8d13-0010c6dffd0f", "datetime");
        m.put("ConceptDatatype:8d4a505e-c2cc-11de-8d13-0010c6dffd0f", "date");
        m.put("ConceptDatatype:8d4a591e-c2cc-11de-8d13-0010c6dffd0f", "time");
        m.put("ConceptDatatype:8d4a4c94-c2cc-11de-8d13-0010c6dffd0f", "notApplicable");
        m.put("ConceptDatatype:8d4a5cca-c2cc-11de-8d13-0010c6dffd0f", "booleanDatatype");

        m.put("ConceptClass:8d492b2a-c2cc-11de-8d13-0010c6dffd0f", "symptomFinding");
        m.put("ConceptClass:8d4918b0-c2cc-11de-8d13-0010c6dffd0f", "diagnosis");
        m.put("ConceptClass:8d491e50-c2cc-11de-8d13-0010c6dffd0f", "question");
        m.put("ConceptClass:8d492774-c2cc-11de-8d13-0010c6dffd0f", "misc");
        m.put("ConceptClass:8d492594-c2cc-11de-8d13-0010c6dffd0f", "convSet");
        m.put("ConceptClass:8d491a9a-c2cc-11de-8d13-0010c6dffd0f", "finding");
        m.put("ConceptClass:8d492954-c2cc-11de-8d13-0010c6dffd0f", "symptom");
        m.put("ConceptClass:8d4923b4-c2cc-11de-8d13-0010c6dffd0f", "medSet");
        m.put("ConceptClass:8d490dfc-c2cc-11de-8d13-0010c6dffd0f", "drug");
        m.put("ConceptClass:8d4907b2-c2cc-11de-8d13-0010c6dffd0f", "test");
        m.put("ConceptClass:8d490bf4-c2cc-11de-8d13-0010c6dffd0f", "procedure");
        m.put("ConceptClass:8d492ee0-c2cc-11de-8d13-0010c6dffd0f", "miscOrder");
        m.put("ConceptClass:8e071bfe-520c-44c0-a89b-538e9129b42a", "frequency");
        m.put("ConceptClass:e30d8601-07f8-413a-9d11-cdfbb28196ec", "unitsOfMeasure");
        m.put("ConceptClass:b4535251-9183-4175-959e-9ee67dc71e78", "pharmacologicDrugClass");
        m.put("ConceptClass:0dcf23d4-3008-4d8e-b12c-4ec95d1cfd97", "medicalSupply");

        m.put("ConceptMapType:35543629-7d8c-11e1-909d-c80aa9edcf4e", "sameAs");
        m.put("ConceptMapType:43ac5109-7d8c-11e1-909d-c80aa9edcf4e", "narrowerThan");
        m.put("ConceptMapType:4b9d9421-7d8c-11e1-909d-c80aa9edcf4e", "broaderThan");

        m.put("ConceptSource:fb9aaaf1-65e2-4c18-b53c-16b575f2f385", "pih");
        m.put("ConceptSource:21ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "ciel");
        m.put("ConceptSource:2889f378-f287-40a5-ac9c-ce77ee963ed7", "loinc");
        m.put("ConceptSource:3f65bd34-26fe-102b-80cb-0017a47871b2", "icd10who");
        m.put("ConceptSource:1ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "snomedCt");
        m.put("ConceptSource:2ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "snomedNp");
        m.put("ConceptSource:13ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "ampath");
        m.put("ConceptSource:ddb6b595-0b85-4a80-9243-efe4ba404eef", "mdrtb");
        m.put("ConceptSource:edd52713-8887-47b7-ba9e-6e1148824ca4", "emrapi");
        m.put("ConceptSource:25ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "imoProcedureIT");
        m.put("ConceptSource:24ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "imoProblemIT");
        m.put("ConceptSource:4ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD", "rxNorm");

        m.put("Locale:en", "Locale.ENGLISH");
        m.put("Locale:fr", "Locale.FRENCH");
        m.put("Locale:es", "locale_SPANISH");
        m.put("Locale:ht", "locale_HAITI");
        return m;
    }

	@RequestMapping(value="/module/metadatatools/conceptBuilderExport")
	public void post(ModelMap model, @RequestParam(required=false) String concepts) throws Exception {

        List<String> linesOfCode = new ArrayList<String>();
        Map<String, String> alreadyDone = new LinkedHashMap<String, String>(); // This ia map from uuid -> variableName
        List<String> conceptIdsOrUuids = new ArrayList<String>();
        List<String> errorMessages = new ArrayList<String>();

        if (StringUtils.isNotEmpty(concepts)) {
            conceptIdsOrUuids.addAll(Arrays.asList(concepts.trim().split("\\s+")));  // Split on any whitespace or newlines
        }

        log.info("Starting export with list of " + conceptIdsOrUuids.size() + " concepts.");

        for (String conceptIdOrUuid : conceptIdsOrUuids) {
            addConceptLine(linesOfCode, alreadyDone, errorMessages, getConcept(conceptIdOrUuid));
        }

        model.addAttribute("concepts", concepts);
        model.addAttribute("conceptIdsOrUuids", conceptIdsOrUuids);
        model.addAttribute("linesOfCode", linesOfCode);
        model.addAttribute("alreadyDone", alreadyDone);
        model.addAttribute("errorMessages", errorMessages);
	}

    /**
     * Adds a line of code for creating a concept using concept builder.  Returns the variable assigned to this concept
      */
	public String addConceptLine(List<String> linesOfCode, Map<String, String> alreadyDone, List<String> errorMessages, Concept c) {
	    String existingVariable = alreadyDone.get(c.getUuid());
	    if (existingVariable != null) {
	        return existingVariable;
        }
        StringBuilder sb = new StringBuilder();
        if (c instanceof org.openmrs.ConceptNumeric) {
            ConceptNumeric cn = (ConceptNumeric) c;
            addLine(sb, "install(new ConceptNumericBuilder(\"", c.getUuid(), "\")");
            if (cn.getUnits() != null) {
                addLine(sb, ".units(\"", cn.getUnits(), "\")");
            }
            if (cn.getPrecise() != null) {
                addLine(sb, ".precise(", cn.getPrecise().toString(), ")");
            }
            if (cn.getHiAbsolute() != null) {
                addLine(sb, ".hiAbsolute(", cn.getHiAbsolute().toString(), ")");
            }
            if (cn.getHiCritical() != null) {
                addLine(sb, ".hiCritical(", cn.getHiCritical().toString(), ")");
            }
            if (cn.getHiNormal() != null) {
                addLine(sb, ".hiNormal(", cn.getHiNormal().toString(), ")");
            }
            if (cn.getLowAbsolute() != null) {
                addLine(sb, ".lowAbsolute(", cn.getLowAbsolute().toString(), ")");
            }
            if (cn.getLowCritical() != null) {
                addLine(sb, ".lowCritical(", cn.getLowCritical().toString(), ")");
            }
            if (cn.getLowNormal() != null) {
                addLine(sb, ".lowNormal(", cn.getLowNormal().toString(), ")");
            }
        }
        else {
            addLine(sb, "install(new ConceptBuilder(\"", c.getUuid(), "\")");
        }

        String dataType = getOverrides().get("ConceptDatatype:" + c.getDatatype().getUuid());
        if (dataType == null) {
            throw new IllegalArgumentException("Unable to find concept datatype in mapping file; " + c.getDatatype().getName() + " - " + c.getDatatype().getUuid());
        }
        addLine(sb, ".datatype(", dataType, ")");

        String conceptClass = getOverrides().get("ConceptClass:" + c.getConceptClass().getUuid());
        if (conceptClass == null) {
            throw new IllegalArgumentException("Unable to find concept class in mapping file; " + c.getConceptClass().getName() + " - " + c.getConceptClass().getUuid());
        }
        addLine(sb, ".conceptClass(", conceptClass, ")");

        List<ConceptName> names = new ArrayList<ConceptName>(c.getNames());
        Collections.sort(names, new Comparator<ConceptName>() {
            public int compare(ConceptName o1, ConceptName o2) {
                int r1 = (o1.getLocalePreferred() != null && o1.getLocalePreferred()) ? -1 : 1;
                int r2 = (o2.getLocalePreferred() != null && o2.getLocalePreferred()) ? -1 : 1;
                if (r1 == r2) {
                    return o1.getLocale().toString().compareTo(o2.getLocale().toString());
                }
                return (r1 < r2) ? -1 : 1;
            }
        });
        for (ConceptName cn : names) {
            String cnt = cn.getConceptNameType() != null ? "ConceptNameType." + cn.getConceptNameType().toString() : "null";
            String cnLocale = getOverrides().get("Locale:" + cn.getLocale());
            if (cnLocale == null) {
                errorMessages.add("Locale " + cn.getLocale() + " not in mapping file.  Not including concept name: " + cn.getUuid());
            }
            else {
                addLine(sb, ".name(\"", cn.getUuid(), "\", \"", cn.getName(), "\", ", cnLocale, ", ", cnt, ")");
            }
        }

        for (ConceptDescription cd : c.getDescriptions()) {
            String cdLocale = getOverrides().get("Locale:" + cd.getLocale());
            if (cdLocale == null) {
                errorMessages.add("Locale " + cd.getLocale() + " not in mapping file.  Not including concept description: " + cd.getUuid());
            }
            else {
                addLine(sb, ".description(\"", cd.getUuid(), "\", \"", escape(cd.getDescription()), "\", ", cdLocale, ")");
            }
        }

        for (ConceptMap cm : c.getConceptMappings()) {
            String conceptMapType = getOverrides().get("ConceptMapType:" + cm.getConceptMapType().getUuid());
            if (conceptMapType == null) {
                throw new IllegalArgumentException("Unable to find concept map type; " + cm.getUuid());
            }
            ConceptReferenceTerm term = cm.getConceptReferenceTerm();
            String conceptSource = getOverrides().get("ConceptSource:" + term.getConceptSource().getUuid());
            if (conceptSource == null) {
                errorMessages.add("Concept Source not in mapping file: " + term.getConceptSource().getName() + "; Not including reference term: " + term.getUuid());
            }
            else {
                addLine(sb, ".mapping(new ConceptMapBuilder(\"", cm.getUuid(), "\")", ".type(", conceptMapType, ")", ".ensureTerm(", conceptSource, ", \"", term.getCode(), "\").build())");
            }
        }

        if (c.getSetMembers() != null && !c.getSetMembers().isEmpty()) {
            StringBuilder setMembers = new StringBuilder();
            for (Concept setMember : c.getSetMembers()) {
                String memberVariableName = addConceptLine(linesOfCode, alreadyDone, errorMessages, setMember);
                setMembers.append(setMembers.length() == 0 ? "" : ", ").append(memberVariableName);
            }
            addLine(sb, ".setMembers(", setMembers.toString(), ")");
        }

        if (c.getAnswers() != null && !c.getAnswers().isEmpty()) {
            StringBuilder answers = new StringBuilder();
            for (ConceptAnswer answer : c.getAnswers()) {
                Concept answerConcept = answer.getAnswerConcept();
                if (answerConcept == null) {
                    errorMessages.add("No support for answers other than concepts (eg. Drugs).  Not adding concept answer: " + answer.getUuid());
                }
                else {
                    String answerVariableName = addConceptLine(linesOfCode, alreadyDone, errorMessages, answerConcept);
                    answers.append(answers.length() == 0 ? "" : ", ").append(answerVariableName);
                }
            }
            addLine(sb, ".answers(", answers.toString(), ")");
        }

        addLine(sb, ".build());");
        addLine(sb, "");

        String variableName = "concept" + c.getConceptId();
        linesOfCode.add("Concept " + variableName + " = " + sb.toString());
        alreadyDone.put(c.getUuid(), variableName);
        return variableName;
    }

    public Concept getConcept(String conceptIdOrUuid) {
        try {
            int conceptId = Integer.parseInt(conceptIdOrUuid);
            Concept c = conceptService.getConcept(conceptId);
            log.info("Found concept by id " + conceptId + ": " + c.getDisplayString());
            return c;
        }
        catch (Exception e) {
            Concept c = conceptService.getConceptByUuid(conceptIdOrUuid);
            if (c != null) {
                log.info("Found concept by uuid " + conceptIdOrUuid + ": " + c.getDisplayString());
                return c;
            }
            throw new IllegalArgumentException("No concept with id or uuid found: " + conceptIdOrUuid);
        }
    }

	public static void addLine(StringBuilder sb, String... line) {
        for (String s : line) {
            sb.append(s);
        }
        sb.append("\n");
    }

    public static String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}
