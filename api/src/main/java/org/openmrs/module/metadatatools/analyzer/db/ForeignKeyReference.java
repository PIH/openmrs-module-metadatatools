package org.openmrs.module.metadatatools.analyzer.db;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForeignKeyReference {

    private TableColumn foreignKey;
    private TableColumn primaryKey;

    public ForeignKeyReference(TableColumn foreignKey, TableColumn primaryKey) {
        this.foreignKey = foreignKey;
        this.primaryKey = primaryKey;
    }
}
