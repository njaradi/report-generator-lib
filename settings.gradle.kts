plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "skr"
include("spec")
include("CSVImpl")
include("TXTImpl")
include("PDFImpl")
include("XLSXImpl")
include("teeestApp")
