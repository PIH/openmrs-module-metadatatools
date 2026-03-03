package org.openmrs.module.metadatatools.analyzer.db;

import lombok.Data;

@Data
public class ForeignKeyReferenceCount extends ForeignKeyReference {

    private long count;

    public ForeignKeyReferenceCount(ForeignKeyReference reference, long count) {
        this.setForeignKey(reference.getForeignKey());
        this.setPrimaryKey(reference.getPrimaryKey());
        this.setCount(count);
    }
}
