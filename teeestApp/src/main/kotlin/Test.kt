import model.FormatName
import spec.ReportGeneratorInterface
import java.io.InputStreamReader
import java.sql.DriverManager
import java.util.*


fun main() {
    val jdbcUrl = "jdbc:mysql://localhost:3306/skrskr_databes";
    val dbUser = "root"
    val dbPassword = ""

//    return try {
//        DriverManager.getConnection(dbUrl, dbUser, dbPassword)
//    } catch (e: SQLException) {
//        e.printStackTrace()
//        null
//    }

    val conn = DriverManager
        .getConnection(jdbcUrl, dbUser, dbPassword)
    println(conn.isValid(0))

    val stmt = conn.createStatement()

    val resultSet = stmt.executeQuery(
        "SELECT grade_id, student_name, course_name, grade, points, date_recorded, comments" +
                "  FROM  student_grades"
    )

    val serviceLoader = ServiceLoader.load(ReportGeneratorInterface::class.java)
    val exporterServices = mutableMapOf<FormatName, ReportGeneratorInterface> ()

    serviceLoader.forEach{ service ->
        exporterServices[service.implName] = service
    }
    println(exporterServices.keys)

//    val data: Map<String, List<String>> = mapOf(
//        "Fruits" to listOf("Apple", "Banana", "Orange"),
//        "Vegetables" to listOf("Carrot", "Broccoli", "Spinach"),
//        "Grains" to listOf("Rice", "Wheat", "Oats")
//    )
//    println(data)
//
//    exporterServices[FormatName.TXT]?.generateReport(data, "izlazProba1.txt", true, "Stuff to eat", "Yep this is stuff to eat.")
    exporterServices[FormatName.TXT]?.generateReport(resultSet, "baza_izlazProba1.txt", true, "Student grades", "They did good")
}