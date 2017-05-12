package com.segniertomato.work.dao;


import java.util.Map;

public class Utils {

    protected interface RowBatchCreator<T> {
        Map<String, T> createRowMap(int i);
    }

    protected static <T> Map<String, T>[] getBatchValues(RowBatchCreator<T> rowBatchCreator, int rowCount) {

        Map<String, T>[] batchValues = new Map[rowCount];
        for (int i = 0; i < rowCount; i++) {
            batchValues[i] = rowBatchCreator.createRowMap(i);
        }

        return batchValues;
    }
}
