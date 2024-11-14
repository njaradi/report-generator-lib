import model.FormatName
import spec.ReportGeneratorInterface
import java.io.File
import java.io.InputStreamReader
import java.sql.DriverManager
import java.util.*


fun main() {
    val jdbcUrl = "jdbc:mysql://localhost:3306/skrskr_databes";
    val dbUser = "root"
    val dbPassword = ""

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
    val config: File = File("format.json")

//    val data: Map<String, List<String>> = mapOf(
//        "Fruits" to listOf("Apple", "Banana", "Orange"),
//        "Vegetables" to listOf("Carrot", "Broccoli", "Spinach"),
//        "Grains" to listOf("Rice", "Wheat", "Oats")
//    )
//    println(data)
//
//    exporterServices[FormatName.TXT]?.generateReport(data, "izlazProba1.txt", true, "Stuff to eat", "Yep this is stuff to eat.")
    val calculate = mapOf(
        "sum" to listOf(
            listOf("points")  // Sum of points across all records
        ),
        "avg" to listOf(
            listOf("grade"),    // Average grade across all records
            listOf("points")    // Average points across all records
        ),
        "cnt" to listOf(
            listOf("grade", ">= 50"),       // Count of grades >= 50 (passing grades)
        ),
        "sub" to listOf(
            listOf("points", "grade")  // Subtract grade from points (hypothetically)
        )
    )
    //exporterServices[FormatName.PDF]?.generateReport(resultSet, "baza_izlazProba1.pdf", true, "[Student grades]", "{%&They&% (did) good}", config, calculate)
    exporterServices[FormatName.TXT]?.generateReport(resultSet, "baza_izlazProba1_calc.txt", true, "[Student grades]", "{%&They&% (did) good}",null, calculate)
}