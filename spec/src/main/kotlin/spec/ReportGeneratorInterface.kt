package spec

import model.FormatName
import java.io.File
import java.sql.ResultSet
import java.sql.ResultSetMetaData

import calculations.Calculations

interface ReportGeneratorInterface {
    //TODO: the project

    val implName: FormatName
    val extension: String

    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        config: File? = null
    )

    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        config: File? = null,
        calculate: Map<String, List<String>>? = null
    )
    {
        //resolve calc
        calculate?.let {
            var calc: Calculations = Calculations()
            calculate["sum"]?.let{

            }
            calculate["sub"]?.let{

            }
            calculate["mul"]?.let{

            }
            calculate["div"]?.let{

            }
            calculate["avg"]?.let{

            }
        }
        generateReport(data, destination, header, title, summary, config)
    }

    //mozda calc
    private fun unpackConfig(config: File){
        //return map<String, list>
        //todo: napraviti privatnu metodu koja raspakujue kofig file, vraca izracunatu kolonu
    }

    fun generateReport(
        data: ResultSet,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null,
        config: File? = null,
        calculate: Map<String, List<String>>? = null
        )
    {
        val preparedData = prepareData(data)
        generateReport(preparedData, destination, header, title, summary, config, calculate)
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