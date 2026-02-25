package org.openmrs.module.metadatatools.initializer;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class CsvExporter implements Exporter {

    /**
     * @return a Map from file path to contents of the csv that should be written to that file path
     */
    abstract Map<String, List<String[]>> getCsvExports();

    @Override
    public void export(ZipOutputStream zos) throws IOException {
        Map<String, List<String[]>> csvFiles = getCsvExports();
        for (String fileName : csvFiles.keySet()) {
            List<String[]> data = csvFiles.get(fileName);
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(zos, StandardCharsets.UTF_8));
            csvWriter.writeAll(data);
            csvWriter.flush();
            zos.closeEntry();
        }
    }
}
