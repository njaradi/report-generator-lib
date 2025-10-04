# Report Generator Library

Developed as a university project as part of our _Software Components course_ for demonstrating modular software design, reusable components, and runtime loading via SPI in Kotlin.

Modular and extensible report generation system built in Kotlin using Gradle, designed as a set of reusable libraries and a command-line program.
The system supports generating reports in multiple formats (CSV, TXT, PDF, Excel) with optional formatting, summaries, and data calculations.

Each report type is implemented as a separate module that follows a Service Provider Interface (SPI) architecture, separating the API specification from its runtime implementations.

## Features:
- Generate formatted and unformatted reports <br>
- Supported formats: CSV, TXT, PDF, Excel <br>
- Table-based data representation (rows & columns) <br>
- Optional header, title, and summary section <br>
- Custom calculations: <br>
- SUM, AVERAGE, COUNT, ADD, SUBTRACT, MULTIPLY, DIVIDE <br>
- Conditional counting (e.g., count rows where a value matches a condition) <br>
- Row numbering <br>
- Support for multiple data sources: <br>
- SQL query results from a database <br>
- In-memory data structures (lists, maps, etc.) <br>
- Formatted output for PDF/Excel: 
- Bold, italic, underline, colored text <br>
- Table borders, header formatting <br>


## Technologies:
Kotlin <br>
Gradle (multi-module project) <br>
Apache POI (Excel generation) <br>
ReportLab / HTML-to-PDF (PDF generation) <br>
JDBC (database access for CLI) <br>
SPI (Service Provider Interface) – dynamic module loading
