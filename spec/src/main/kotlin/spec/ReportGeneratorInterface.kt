package spec

import model.FormatName
import java.io.File
import java.sql.ResultSet
import java.sql.ResultSetMetaData

interface ReportGeneratorInterface {
    //TODO: the project

    val implName: FormatName
    val extension: String

    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: String? = null
    )

//    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: String? = null, config: File){
//        val result = unpackConfig(config)
//        *kalkulacije* -->result_calced (jedna kolona vrv)
//
//        val konfig = "sum 1,2"
//
//        sub(data[1], data[2])
//
//        //data += result_calced
//        //generateReport(data..) //bez config
//        //todo: generate report, config
//    }

//    //mozda calc
//    private fun unpackConfig(config: File){
//        //return map<String, list>
//        //todo: napraviti privatnu metodu koja raspakujue kofig file, vraca izracunatu kolonu
//    }

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