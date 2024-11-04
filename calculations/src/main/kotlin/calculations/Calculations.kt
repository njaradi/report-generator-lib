package calculations

import kotlin.collections.Map

interface Calculations {

    // todo napisati dokumentaciju
    private fun castData (data: List<String>) : MutableList<Double>{
        try{
            return data.map { it.toDouble() }.toMutableList()
        }catch (e : Exception){
            throw Exception("could not cast list from String to Double")
        }
    }

    //column
    fun sub(){
        //2 kolone
    }

    fun multiply(){
        //vise kolona
    }

    fun divide(){
        //2 kolone
    }

//    fun sum(a: String) : Map<String, List<String>>
//    {
//        val result: Map<String, List<String>>
//        //vise kolona - sabira sve kolone u 1
//        return result
//    }


    //summary

    // = 5, >=5,
    private fun condition(data: List<Double>, condition : String) : List<Double>
    {
        // Extract operator and threshold from condition
        val operator = condition.takeWhile { !it.isDigit() && it != '.' && it != '-' }
        val value : Double = condition.removePrefix(operator).toDoubleOrNull() ?: return emptyList()

        // Filter the data based on the operator
        return when (operator) {
            ">" -> data.filter { it > value }
            ">=" -> data.filter { it >= value }
            "<" -> data.filter { it < value }
            "<=" -> data.filter { it <= value }
            "==" -> data.filter { it == value }
            "!=" -> data.filter { it != value }
            else -> emptyList()
        }
    }

    private fun condition(data: List<String>, condition : String) : List<String>
    {
        // Split the condition into parts (operator and value)
        val parts = condition.split(" ", limit = 2)
        if (parts.size < 2) return emptyList() // Return empty list if the condition format is invalid

        val operator = parts[0]
        val value = parts[1]

        // Filter the data based on the operator
        return when (operator) {
            "equals" -> data.filter { it == value }
            "contains" -> data.filter { it.contains(value) }
            else -> emptyList() // Return empty list for unsupported operators
        }
    }

    fun sum(data: List<String>, condition : String? = null){
        sum(castData(data))
    }

    fun sum(data: List<Double>, condition: String? = null) : Double{
        var filteredData = data.toMutableList()
        if(condition != null){
            filteredData = condition(data, condition).toMutableList()
        }

        var result: Double = 0.0
        filteredData.forEach{
            result += it
        }
        return result
    }

    fun average(data: List<String>){
        average(castData(data))
    }

    fun average(data: List<Double>, condition: String? = null) : Double{
        var filteredData = data.toMutableList()
        if(condition != null){
            filteredData = condition(data, condition).toMutableList()
        }

        var result: Double = 0.0
        filteredData.forEach{
            result += it
        }
        return result/data.size
    }


    fun count(data: List<String>, condition: String? = null) : Int{
        if(condition != null){
            if(condition.contains("contains") || condition.contains("equals"))
            {
                return condition(data,condition).size
            }else{
                return condition(castData(data), condition).size
            }
        }
        return data.size
    }

}