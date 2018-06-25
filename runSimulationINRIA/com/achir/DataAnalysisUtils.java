package com.achir;

import java.io.File;
import java.util.Map;
import java.io.*;
import java.util.TreeMap;

public class DataAnalysisUtils<T, U, R> {
    public String separator = " ";

    public void mapToTxtFile(Map<T,Map<U,R>> map, String fileName) {
        Map<T, Map<U,R>> treeMap = new TreeMap<>(map);
        File file =  new File(fileName) ;
        Writer writer = null ;
        try {
            writer =  new FileWriter(file) ;
            // write here
            for (T key : treeMap.keySet()) {
                StringBuffer lineToWrite = new StringBuffer(String.valueOf(key));
                for (U valuekey : treeMap.get(key).keySet()) {
                    lineToWrite
                            .append(separator)
                            .append(String.valueOf(valuekey))
                            .append(separator)
                            .append(String.valueOf(treeMap.get(key).get(valuekey)));
                }
                lineToWrite.append("\n");
                writer.write(lineToWrite.toString());
            }
        }  catch (IOException e) {
            System.out.println("Error " + e.getMessage()) ;
            e.printStackTrace() ;
        }  finally {
            if (writer != null) {
                try {
                    writer.close() ;
                }  catch (IOException e) {
                    System.out.println("Error " + e.getMessage()) ;
                    e.printStackTrace() ;
                }
            }
        }
    }
}
