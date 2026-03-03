package org.openmrs.module.metadatatools.analyzer.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableColumn {

    private String table;
    private String column;

}
