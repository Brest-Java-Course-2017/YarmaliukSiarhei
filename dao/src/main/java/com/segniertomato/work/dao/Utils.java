package com.segniertomato.work.dao;


import com.segniertomato.work.model.Pair;

import java.util.*;

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


    static <K> Pair<List<K>, List<K>> getNotEqualsElementsInLists(List<K> firstList, List<K> secondList) {

        Set<K> firstCandidate = new HashSet<>(firstList);
        Set<K> secondCandidate = new HashSet<>(secondList);

        Iterator<K> firstIterator = firstCandidate.iterator();

        while (firstIterator.hasNext()) {

            K firstItem = firstIterator.next();
            Iterator<K> secondIterator = secondCandidate.iterator();

            while (secondIterator.hasNext()) {
                K secondItem = secondIterator.next();

                if (secondItem.equals(firstItem)) {
                    firstIterator.remove();
                    secondIterator.remove();
                    break;
                }
            }
        }

        return new Pair<>(new ArrayList<K>(firstCandidate), new ArrayList<K>(secondCandidate));
    }
}
