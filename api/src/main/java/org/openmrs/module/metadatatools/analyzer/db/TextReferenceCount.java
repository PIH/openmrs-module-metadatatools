package org.openmrs.module.metadatatools.analyzer.db;

import lombok.Data;

@Data
public class TextReferenceCount extends TextReference {

    private long count;

    public TextReferenceCount(TextReference textReference, long count) {
        this.setColumn(textReference.getColumn());
        this.setText(textReference.getText());
        this.count = count;
    }
}
