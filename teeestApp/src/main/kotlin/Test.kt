import model.FormatName
import spec.ReportGeneratorInterface
import java.io.InputStreamReader
import java.util.*


fun main() {
    val serviceLoader = ServiceLoader.load(ReportGeneratorInterface::class.java)
    val exporterServices = mutableMapOf<FormatName, ReportGeneratorInterface> ()

    serviceLoader.forEach{ service ->
        exporterServices[service.implName] = service
    }
    println(exporterServices.keys)

//    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
//    val reader = InputStreamReader(inputStream)
//    //val data = prepareData(reader)
//    reader.close()
    val data: Map<String, List<String>> = mapOf(
        "Fruits" to listOf("Apple", "Banana", "Orange"),
        "Vegetables" to listOf("Carrot", "Broccoli", "Spinach"),
        "Grains" to listOf("Rice", "Wheat", "Oats")
    )
    println(data)

    exporterServices[FormatName.TXT]?.generateReport(data, "izlazProba1.txt", true)

}