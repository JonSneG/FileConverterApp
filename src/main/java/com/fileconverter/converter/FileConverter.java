package com.fileconverter.converter;

import java.io.File;

public interface FileConverter {
    void convertToPdf(File inputFile, File outputFile) throws Exception;
    void convertFromPdf(File inputFile, File outputFile) throws Exception;
    boolean supports(String fileExtension);
}