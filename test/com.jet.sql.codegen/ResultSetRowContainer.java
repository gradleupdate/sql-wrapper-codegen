package com.jet.sql.codegen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates the logic to extract tuple attributes from the result set.
 *
 * @author tgorthi
 * @since December 2019
 */
public abstract class ResultSetRowContainer implements Iterator<ResultSetRowContainer>
{
    private final ResultSet resultSet;
    private Map<String, Integer> columnNamesByIndex;

    public ResultSetRowContainer(final ResultSet resultSet)
    {
        this.resultSet = resultSet;
        this.columnNamesByIndex = _populateColumnIndices(resultSet);
    }

    @Override
    public boolean hasNext()
    {
        try
        {
            return resultSet.next();
        }
        catch (SQLException var2)
        {
            throw new RuntimeException("Unable to access result set", var2);
        }
    }

    @Override
    public ResultSetRowContainer next()
    {
        return this;
    }

    /**
     * Populates a Map of columns expected in the result set to the index of the column.
     * This will speed up the lookup process , since calling getColumn by colName will lookup the index every time.
     *
     * @param resultSet -> result set returned by the {@link java.sql.PreparedStatement}
     * @return a {@link HashMap} that returns a mapping of column name to the index in the {@link ResultSet}
     * @throws RuntimeException from any database exception.
     */
    private static Map<String, Integer> _populateColumnIndices(final ResultSet resultSet)
    {
        Objects.requireNonNull(resultSet, "Result set is null.");

        final Map<String, Integer> columnNamesByIndex = new HashMap<>();
        try
        {
            final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            final int numberOfColumns = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++)
            {
                columnNamesByIndex.put(resultSetMetaData.getColumnName(i), i);
            }

        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }

        return columnNamesByIndex;
    }

    /**
     *
     * @param colName -> name of the column in the result set.
     * @return -> returns the index of the column in the result set.
     */
    private int _getColumnIndex(final String colName)
    {
        final Integer index = columnNamesByIndex.get(colName);
        Objects.requireNonNull(index, "Invalid column name" + colName);
        return index;
    }

    /**
     * @param columnLabel -> name of the column in the result set.
     * @return the value of the attribute in the database tuple.
     */
    protected Object getValueByName(String columnLabel)
    {
        try
        {
            return resultSet.getObject(_getColumnIndex(columnLabel));
        }
        catch (SQLException ex)
        {
            throw new RuntimeException("Unable to fetch column " + columnLabel, ex);
        }
    }
}
