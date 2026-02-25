package org.openmrs.module.metadatatools.initializer;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface Exporter {

    void export(ZipOutputStream zos) throws IOException;

}
