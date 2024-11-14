package xlsx

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import model.FormatName
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import spec.ReportGeneratorInterface
import java.io.File
import java.io.FileOutputStream

class XLSXReportImpl : ReportGeneratorInterface {
    override val implName: FormatName = FormatName.XLSX
    override val extension: String = ".xlsx"

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: String?,
        config: File?
    ) {
        val formatRules = config?.let { loadFormatRules(it) }
        // todo: popravi
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report")

        // Add title if provided
        title?.let {
            val titleRow: Row = sheet.createRow(0)
            val titleCell: Cell = titleRow.createCell(0)

            // Apply rich text formatting
            val richTitleText = if (formatRules != null) {
                applyRichTextFormatting(it, formatRules, workbook)
            } else {
                XSSFRichTextString(it).apply {
                    val defaultFont = workbook.createFont().apply {
                        bold = true
                        fontHeightInPoints = 18
                    }
                    this.applyFont(defaultFont)
                }
            }

            titleCell.setCellValue(richTitleText)

            // Merge title cells across the entire row
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, data.size - 1))
        }




        // Create header row if necessary
        if (header) {
            val headerRow: Row = sheet.createRow(1)
            data.keys.forEachIndexed { index, columnName ->
                headerRow.createCell(index).setCellValue(columnName)
            }
        }

        // Add data rows
        val numRows = data.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1) // Adjust for header
            data.keys.forEachIndexed { index, columnName ->
                dataRow.createCell(index).setCellValue(data[columnName]?.get(i) ?: "")
            }
        }

        // Add summary if provided
        summary?.let {
            val summaryRow: Row = sheet.createRow(sheet.lastRowNum + 2)
            val summaryCell: Cell = summaryRow.createCell(0)

            // Apply rich text formatting
            val richSummaryText = if (formatRules != null) {
                applyRichTextFormatting(it, formatRules, workbook)
            } else {
                XSSFRichTextString(it).apply {
                    val defaultFont = workbook.createFont().apply {
                        italic = true
                    }
                    this.applyFont(defaultFont)
                }
            }

            summaryCell.setCellValue(richSummaryText)

            // Merge summary cells across the entire row
            sheet.addMergedRegion(CellRangeAddress(summaryRow.rowNum, summaryRow.rowNum, 0, data.size - 1))
        }



        // Write to the destination file
        FileOutputStream(destination).use { outputStream ->
            workbook.write(outputStream)
        }

        // Closing the workbook
        workbook.close()
    }

    private fun loadFormatRules(config: File): Map<String, String> {
        val jsonContent = config.readText()
        return Json.decodeFromString(jsonContent)
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

    private fun createCellStyle(
        workbook: Workbook,
        style: String
    ): CellStyle {
        val cellStyle = workbook.createCellStyle()
        val font = workbook.createFont()

        if ("bold" in style) font.bold = true
        if ("italic" in style) font.italic = true
        if ("underline" in style) font.underline = Font.U_SINGLE

        if ("pink" in style) font.color = IndexedColors.PINK.index.toShort()

        val sizeMatch = Regex("\\d+").find(style)
        if (sizeMatch != null) font.fontHeightInPoints = sizeMatch.value.toShort()

        cellStyle.setFont(font)
        return cellStyle
    }

    fun applyRichTextFormatting(
        text: String,
        formatRules: Map<String, String>,
        workbook: Workbook
    ): XSSFRichTextString {
        val richText = XSSFRichTextString()
        var currentIndex = 0

        // Iterate through each tag in the format rules and apply the formatting
        for ((tag, style) in formatRules) {
            while (true) {
                val startIdx = text.indexOf(tag, currentIndex)
                val endIdx = text.indexOf(tag.reversed(), startIdx + tag.length)

                if (startIdx == -1 || endIdx == -1) break

                // Add unformatted text before the current tag
                if (currentIndex < startIdx) {
                    richText.append(text.substring(currentIndex, startIdx))
                }

                // Extract the segment inside the tags
                val segment = text.substring(startIdx + tag.length, endIdx)
                currentIndex = endIdx + tag.length

                // Apply styles based on the configuration
                val segmentFont = workbook.createFont().apply {
                    if ("bold" in style) bold = true
                    if ("italic" in style) italic = true
                    if ("underline" in style) underline = Font.U_SINGLE
                    if ("pink" in style) color = IndexedColors.PINK.index.toShort()

                    // Set font size if specified
                    val sizeMatch = Regex("\\d+").find(style)
                    if (sizeMatch != null) fontHeightInPoints = sizeMatch.value.toShort()
                }

                // Create and apply the formatted segment
                val formattedSegment = XSSFRichTextString(segment)
                formattedSegment.applyFont(segmentFont)
                richText.append(segment, segmentFont as XSSFFont?)
            }
        }

        // Append any remaining unformatted text
        if (currentIndex < text.length) {
            richText.append(text.substring(currentIndex))
        }

        return richText
    }
}
