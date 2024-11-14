import model.FormatName
import spec.ReportGeneratorInterface
import java.io.File
import java.io.InputStreamReader
import java.sql.DriverManager
import java.util.*


// Parses user input for the calculate parameter
fun parseCalculateInput(input: String?): Map<String, List<List<String>>>? {
    if (input.isNullOrEmpty()) return null

    val calculateMap = mutableMapOf<String, List<List<String>>>()

    // Use a regex to match each operation block, e.g., [sum col1,col2;col3]
    val operationPattern = "\\[(\\w+) ([^\\]]+)]".toRegex()

    // Iterate over each matched operation block
    for (match in operationPattern.findAll(input)) {
        val operation = match.groupValues[1] // Extracts "sum" or "avg"
        val columnsPart = match.groupValues[2] // Extracts "col1,col2;col3" or "col2"

        // Split columns by semicolon for multiple lists, e.g., "col1,col2;col3" -> ["col1,col2", "col3"]
        val columnLists = columnsPart.split(";").map { listString ->
            // Split each list by commas to get individual columns
            listString.split(",").map { it.trim() }
        }

        // Add the parsed operation and column lists to the map
        calculateMap[operation] = columnLists
    }

    return calculateMap
}

// Prompts user to select a format and ensures valid input
fun selectFormat(): FormatName {
    while (true) {
        val formatInput = readLine()?.uppercase()
        try {
            return FormatName.valueOf(formatInput ?: "")
        } catch (e: IllegalArgumentException) {
            println("Invalid format. Please choose one of the following: CSV, TXT, PDF, XLSX.")
        }
    }
}

fun main() {

    val serviceLoader = ServiceLoader.load(ReportGeneratorInterface::class.java)
    val exporterServices = mutableMapOf<FormatName, ReportGeneratorInterface> ()

    serviceLoader.forEach{ service ->
        exporterServices[service.implName] = service
    }
    println(exporterServices.keys)

    var running : Boolean = true
    while (running) {
        println("Welcome to the Report Generator!")

        // Placeholder for data (you'd replace this with actual ResultSet from your database)
        //val data: ResultSet = getDummyResultSet()
        println("Enter the database URL for the report file:")
        val jdbcUrl = readLine() ?: ""
        val dbUser = "root"
        val dbPassword = ""

        val conn = DriverManager
            .getConnection(jdbcUrl, dbUser, dbPassword)
        println(conn.isValid(0))
        val stmt = conn.createStatement()

        println("Enter the SQL SELECT query:")
        val sql_query = readLine() ?: ""

        val resultSet = stmt.executeQuery(sql_query)
//        val resultSet = stmt.executeQuery(
//            "SELECT grade_id, student_name, course_name, grade, points, date_recorded, comments" +
//                    "  FROM  student_grades"
//        )

        // Prompt user for each parameter
        println("Choose the format for the report (CSV, TXT, PDF, XLSX):")
        val format = selectFormat()

        // Prompt user for each parameter
        println("Enter the destination path for the report file:")
        val destination = readLine() ?: ""

        println("Include header? (yes/no)")
        val header = when (readLine()?.lowercase()) {
            "yes" -> true
            "no" -> false
            else -> {
                println("Invalid input. Defaulting to 'no'.")
                false
            }
        }

        println("Enter title for the report (or press Enter to skip):")
        val title = readLine()?.takeIf { it.isNotEmpty() }

        println("Enter summary for the report (or press Enter to skip):")
        val summary = readLine()?.takeIf { it.isNotEmpty() }

        println("Enter the path to a config file (or press Enter to skip):")
        val configPath = readLine()
        val config = configPath?.takeIf { it.isNotEmpty() }?.let { File(it) }

        println("Enter calculations (format: operation column1,column2; e.g., sum col1,col2):")
        val calculateInput = readLine()
        val calculate = parseCalculateInput(calculateInput)


        // Call generateReport with user-provided parameters
        exporterServices[format]?.generateReport(resultSet, destination, header, title, summary, config, calculate)

        println("Report generated successfully! Would you like to create another report? (yes/no)")
        running = readLine()?.lowercase() == "yes"
    }
    println("Goodbye!")
/*
//    val jdbcUrl = "jdbc:mysql://localhost:3306/skrskr_databes";
//    val dbUser = "root"
//    val dbPassword = ""
//
//    val conn = DriverManager
//        .getConnection(jdbcUrl, dbUser, dbPassword)
//    println(conn.isValid(0))
//
//    val stmt = conn.createStatement()
//
//    val resultSet = stmt.executeQuery(
//        "SELECT grade_id, student_name, course_name, grade, points, date_recorded, comments" +
//                "  FROM  student_grades"
//    )
//
//    val serviceLoader = ServiceLoader.load(ReportGeneratorInterface::class.java)
//    val exporterServices = mutableMapOf<FormatName, ReportGeneratorInterface> ()
//
//    serviceLoader.forEach{ service ->
//        exporterServices[service.implName] = service
//    }
//    println(exporterServices.keys)
//    val config: File = File("format.json")
//
//    val calculate = mapOf(
//        "sum" to listOf(
//            listOf("points")  // Sum of points across all records
//        ),
//        "avg" to listOf(
//            listOf("grade"),    // Average grade across all records
//            listOf("points")    // Average points across all records
//        ),
//        "cnt" to listOf(
//            listOf("grade", ">= 50"),       // Count of grades >= 50 (passing grades)
//        ),
//        "sub" to listOf(
//            listOf("points", "grade")  // Subtract grade from points (hypothetically)
//        )
//    )
//    exporterServices[FormatName.PDF]?.generateReport(resultSet, "baza_izlazProba1_calc.pdf", true, "[Student grades]", "{%&They&% (did) good}",config, calculate)
*/
}