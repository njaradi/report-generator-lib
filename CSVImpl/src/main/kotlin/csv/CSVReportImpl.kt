package csv

import model.FormatName
import spec.ReportGeneratorInterface

import java.io.File

class CSVReportImpl : ReportGeneratorInterface {
    override val implName: FormatName = FormatName.CSV
    override val extension: String = ".csv"

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?
    ) {
        val columns = data.keys.toList()
        val numRows = data.values.first().size

        // Write to CSV file
        File(destination).printWriter().use { writer ->
            if(header)
                writer.println(columns.joinToString(","))  // Write the header
            for (i in 0 until numRows) {
                val row = columns.map { column -> data[column]?.get(i) ?: "" }
                writer.println(row.joinToString(","))   // Write each row
            }
        }
    }
}