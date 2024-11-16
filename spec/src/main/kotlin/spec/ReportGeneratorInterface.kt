package spec

import model.FormatName
import java.io.File
import java.sql.ResultSet
import java.sql.ResultSetMetaData

import calculations.Calculations

/**
 * An interface for generating formatted or non-formatted reports from a map of column data to different formats.
 *
 * Implementations of this interface should define how the report is formatted and saved.
 */
interface ReportGeneratorInterface {
    //TODO: the project

    val implName: FormatName
    val extension: String

    /**
     * Generates a report based on the provided data and writes it to the specified destination.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     *             All lists in the map should have the same size to ensure proper row alignment.
     * @param destination The file path where the report will be saved.
     * @param header Indicates if header is provided in data
     * @param title An optional title for the report, used only in the formatted reports.
     * @param summary An optional summary for the report, used only in the formatted reports.
     * @param config An optional configuration for the report, used only in the formatted reports.
     */
    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        config: File? = null
    )

    /**
     * Generates a report based on the provided data and writes it to the specified destination.
     *
     * @param data A map where the key is the column name and the value is a list of strings representing the column data.
     *             All lists in the map should have the same size to ensure proper row alignment.
     * @param destination The file path where the report will be saved.
     * @param header Indicates if header is provided in data
     * @param title An optional title for the report, used only in the formatted reports.
     * @param summary An optional summary for the report, used only in the formatted reports.
     * @param config An optional configuration for the report, used only in the formatted reports.
     * @param calculate A map where key is a calculation name (e.g. sum,avg...) and
     * value is a list of column names upon which the calculation is done (one calculation can be done on multiple lists of columns)
     */
    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        config: File? = null,
        calculate: Map<String, List<List<String>>>? = null
    ) {
        //resolve calc
        val calculations = Calculations()
        val newData = mutableMapOf<String, List<String>>()
        var updatedSummary : String = summary ?: ""
        val combinedData = data.toMutableMap()
        calculate?.let {
            calculate["sum"]?.let { sumList ->
                for (calcItem in sumList) {
                    // If calcItem has only one item, sum the single column
                    if (calcItem.size == 1) {
                        data[calcItem[0]]?.let { columnData ->
                            // Calculate sum for a single column
                            val result = calculations.sumString(columnData)
                            //newData[calcItem[0]] = result todo videti kako da dodam u summary
                            val key = "sum_"+calcItem[0]
                            updatedSummary += "\n$key: $result"
                        }
                    }
                    // If calcItem has multiple items, create a list of columns and sum them
                    else {
                        val columnsData = calcItem.mapNotNull { columnName ->
                            data[columnName]
                        }
                        if (columnsData.size == calcItem.size) { // Ensure all columns were found in data
                            val result = calculations.sumString(columnsData)
                            newData["sum_" + calcItem.joinToString("_")] = result
                        }
                    }
                }
            }
            calculate["sub"]?.let { subList ->
                for (calcItem in subList) {
                    // Ensure calcItem has exactly two items for the minuend and subtrahend
                    if (calcItem.size == 2) {
                        val minuend = data[calcItem[0]]
                        val subtrahend = data[calcItem[1]]

                        if (minuend != null && subtrahend != null) {
                            // Perform the subtraction using subString
                            val result = calculations.subString(minuend, subtrahend)
                            // Store the result in newData with a descriptive key
                            newData["sub_${calcItem[0]}_${calcItem[1]}"] = result
                        }
                    }
                }
            }

            calculate["mul"]?.let { mulList ->
                for (calcItem in mulList) {
                    val columnsData = calcItem.mapNotNull { columnName ->
                        data[columnName]
                    }
                    if (columnsData.size == calcItem.size) { // Ensure all columns were found in data
                        val result = calculations.sumString(columnsData)
                        newData["mul_" + calcItem.joinToString("_")] = result
                    }

                }
            }
            calculate["div"]?.let { divList ->
                for (calcItem in divList) {
                    // Ensure calcItem has exactly two items for the minuend and subtrahend
                    if (calcItem.size == 2) {
                        val dividend = data[calcItem[0]]
                        val divisor = data[calcItem[1]]

                        if (dividend != null && divisor != null) {
                            // Perform the subtraction using subString
                            val result = calculations.divideString(dividend, divisor)
                            // Store the result in newData with a descriptive key
                            newData["sub_${calcItem[0]}_${calcItem[1]}"] = result
                        }
                    }
                }

            }
            calculate["avg"]?.let {avgList ->
                for (calcItem in avgList) {
                    // Ensure calcItem has only one item for the column to average
                    if (calcItem.size == 1) {
                        data[calcItem[0]]?.let { columnData ->
                            // Perform the average calculation using avgString
                            val result = calculations.averageString(columnData)
                            val key = "avg_${calcItem[0]}"
                            updatedSummary += "\n$key: $result"
                            //newData["avg_${calcItem[0]}"] = result todo videti kako u summary dodati average
                        }
                    }
                }
            }
            calculate["cnt"]?.let { countList ->
                for (calcItem in countList) {
                    if (calcItem.size == 1) {
                        data[calcItem[0]]?.let { columnData ->
                            val result = calculations.count(columnData)
                            val key = "cnt_${calcItem[0]}"
                            updatedSummary += "\n$key: $result"
                        }
                    }
                    else if (calcItem.size == 2){
                        data[calcItem[0]]?.let { columnData ->
                            val columnName = calcItem[0]
                            val condition = calcItem[1]
                            val result = calculations.count(columnData, condition)
                            val key = "cnt_${columnName}_${condition}"
                            updatedSummary += "\n$key: $result"
                        }
                    }
                }
            }
            combinedData.putAll(newData)

        }
        generateReport(combinedData, destination, header, title, updatedSummary, config)
    }

    /**
     * Generates a report based on the provided data and writes it to the specified destination.
     *
     * @param data A table of data representing a database result set, which is usually generated by executing
     *             a statement that queries the database.
     * @param destination The file path where the report will be saved.
     * @param header Indicates if header is provided in data
     * @param title An optional title for the report, used only in the formatted reports.
     * @param summary An optional summary for the report, used only in the formatted reports.
     * @param config An optional configuration for the report, used only in the formatted reports.
     * @param calculate A map where key is a calculation name (e.g. sum,avg...) and
     * value is a list of column names upon which the calculation is done (one calculation can be done on multiple lists of columns)
     */
    fun generateReport(
        data: ResultSet,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        config: File? = null,
        calculate: Map<String, List<List<String>>>? = null
    ) {
        val preparedData = prepareData(data)
        generateReport(preparedData, destination, header, title, summary, config, calculate)
    }



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
