package org.openmrs.module.metadatatools.analyzer.db;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TextReference {

    private TableColumn column;
    private String text;

    public TextReference(TableColumn column, String text) {
        this.column = column;
        this.text = text;
    }
}
