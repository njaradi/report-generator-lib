package org.example.spec

import java.sql.ResultSet
import java.sql.ResultSetMetaData

interface ReportGeneratorInterface {
    //TODO: the project
    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: String? = null)


    fun generateReport(data: ResultSet, destination: String, header: Boolean, title: String? = null, summary: String? = null){
        val preparedData = prepareData(data)
        generateReport(preparedData, destination, header, title, summary)
    }


    /** */
    private fun prepareData(resultSet: ResultSet): Map<String, List<String>> {
        val reportData = mutableMapOf<String, MutableList<String>>()

        val metaData: ResultSetMetaData = resultSet.metaData
        val columnCount = metaData.columnCount

        for (i in 1..columnCount) {
            val columnName = metaData.getColumnName(i)
            reportData[columnName] = mutableListOf()
        }

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val columnName = metaData.getColumnName(i)
                reportData[columnName]!!.add(resultSet.getString(i))
            }
        }

        return reportData
    }
}