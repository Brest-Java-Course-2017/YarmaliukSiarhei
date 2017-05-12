package com.segniertomato.work.database.trigger;


import org.h2.api.Trigger;

import java.sql.*;


public class InvestigationInsertTrigger implements Trigger {

    private static final String SQL_QUERY = "SELECT MAX(investigation_number) FROM investigation";

    private static final int sFirstInvestigationNumber = 1;

    @Override
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {

    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {

        if (newRow[1] != null) return;

        try (PreparedStatement statement = connection.prepareStatement(SQL_QUERY)) {

            ResultSet resultSet = statement.executeQuery();

            String returnedValue = null;
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                System.out.println("Move ResultSet cursor forward to one position");
                returnedValue = resultSet.getString(1);
            }

            newRow[1] = returnedValue != null ? Integer.valueOf(returnedValue) + 1 : sFirstInvestigationNumber;
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {

    }
}
