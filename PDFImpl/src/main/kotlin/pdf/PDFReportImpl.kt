package pdf

import model.FormatName
import spec.ReportGeneratorInterface
import java.io.FileOutputStream
import com.lowagie.text.*
import com.lowagie.text.html.simpleparser.HTMLWorker
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import kotlinx.serialization.json.Json
import java.io.File

import kotlinx.serialization.*
import java.io.StringReader


class PDFReportImpl : ReportGeneratorInterface {
    override val implName: FormatName = FormatName.PDF
    override val extension: String = ".pdf"

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
        config: File?
    ) {
        val formatRules = config?.let { loadFormatRules(it) }
        // Create a new document
        val document = Document()

        try {
            // Initialize PdfWriter
            PdfWriter.getInstance(document, FileOutputStream(destination))

            // Open the document for writing
            document.open()

            // Add title if provided
            title?.let {
                val formattedTitle = formatRules?.let { rules -> applyFormatting(it, rules) } ?: it
                val titleReader = StringReader("<h1 style='text-align:center;'>$formattedTitle</h1><br>")
                HTMLWorker(document).parse(titleReader)
            }

            // Create a table based on the number of columns in the data
            val columns = data.keys.toList()
            val numColumns = columns.size
            val table = PdfPTable(numColumns)

            // Add header row if necessary
            if (header) {
                columns.forEach { column ->
                    val cell = PdfPCell(Paragraph(column, FontFactory.getFont(FontFactory.HELVETICA_BOLD)))
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }

            // Add data rows
            val numRows = data.values.first().size
            for (i in 0 until numRows) {
                columns.forEach { column ->
                    val cellData = data[column]?.get(i) ?: ""
                    table.addCell(cellData)
                }
            }

            // Add the table to the document
            document.add(table)

            // Add summary if provided
            summary?.let {
                val formattedSummary = formatRules?.let { rules -> applyFormatting(it, rules) } ?: it
                val summaryReader = StringReader("<p>$formattedSummary</p>")
                HTMLWorker(document).parse(summaryReader)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close the document
            document.close()
        }
    }

    private fun applyFormatting(text: String, rules: Map<String, String>): String {
        var formattedText = text
        rules.forEach { (tag, style) ->
            when {
                "bold" in style -> formattedText = formattedText.replace(tag, "<b>").replace(mirrored(tag), "</b>")
                "italic" in style -> formattedText = formattedText.replace(tag, "<i>").replace(mirrored(tag), "</i>")
                "underline" in style -> formattedText = formattedText.replace(tag, "<u>").replace(mirrored(tag), "</u>")
                "pink" in style -> formattedText = formattedText.replace(tag, "<span style='color:pink;'>").replace(mirrored(tag),"</span>")
                    .replace(tag.reversed(), "</span>")
                Regex("\\d+").containsMatchIn(style) -> {
                    val size = Regex("\\d+").find(style)?.value
                    formattedText = formattedText.replace(tag, "<span style='font-size:${size}px;'>")
                        .replace(tag.reversed(), "</span>")
                }
            }
        }
        return formattedText
    }

    private fun mirrored(tag: String): String{
        when(tag) {
            "(" -> return ")"
            "[" -> return "]"
            "{" -> return "}"
            "\\" -> return "/"
            "/" -> return "\\"
            "p" -> return "q"
            "q" -> return "p"
        }
        return tag.reversed()
    }

    private fun loadFormatRules(config: File): Map<String, String> {
        val jsonContent = config.readText()
        return Json.decodeFromString(jsonContent)
    }

}