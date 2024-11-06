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

    private fun castData (data: List<List<String>>) : List<List<Double>>{
        try{
            return data.map { innerList ->
                innerList.map { it.toDouble() }
            }
        }catch (e : Exception){
            throw Exception("could not cast list list from String to Double")
        }
    }

    private fun castData (data: List<List<Double>>) : List<List<String>>{
        try{
            return data.map { innerList ->
                innerList.map { it.toString() }
            }
        }catch (e : Exception){
            throw Exception("could not cast list list from Double to String")
        }
    }

    private fun castData (data: List<Double>) : MutableList<String>{
        try{
            return data.map { it.toString() }.toMutableList()
        }catch (e : Exception){
            throw Exception("could not cast list from Double to String")
        }
    }


    //column

    fun sub(minuend: List<String>, subtrahend: List<String>): List<String> {
        val difference: List<Double> = sub(castData(minuend),castData(subtrahend))
        return castData(difference).toList()
    }

    fun sub(minuend: List<Double>, subtrahend: List<Double>): List<Double> {
        val difference: MutableList<Double> = minuend.toMutableList()
        for(i in 0..minuend.size)
        {
             difference[i] = minuend[i]-subtrahend[i]
        }
        return difference.toList()
    }

    fun multiply(multipliers: List<List<String>>) : List<String>{
        return castData(multiply(castData(multipliers)))
    }

    fun multiply(multipliers: List<List<Double>>) : List<Double>{
        val product = MutableList(multipliers[0].size) { 1.0 }
        for ((i, multiplier) in multipliers.withIndex()) {
            product[i] *= multiplier[i]
        }
        return product.toList()
    }

    fun divide(dividend: List<String>, divisor: List<String>): List<String> {
        val quotient: List<Double> = divide(castData(dividend),castData(divisor))
        return castData(quotient).toList()
    }

    fun divide(dividend: List<Double>, divisor: List<Double>): List<Double> {
        val quotient: MutableList<Double> = dividend.toMutableList()
        for(i in 0..dividend.size)
        {
            quotient[i] = dividend[i]/divisor[i]
        }
        return quotient.toList()
    }

    fun sum(addends: List<List<String>>) : List<String>{
        return castData(sum(castData(addends)))
    }

    fun sum(addends: List<List<Double>>) : List<Double>{
        val sum = MutableList(addends[0].size) { 0.0 }
        for ((i, addend) in addends.withIndex()) {
            sum[i] += addend[i]
        }
        return sum.toList()
    }


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
    /**
     * Sums all the values from a list that can be filtered optionally
     *
     * @param data A list of strings that will be parsed into doubles
     * @param condition An optional condition that will filter the list
     */
    fun sum(data: List<String>, condition : String? = null){
        sum(castData(data))
    }

    /**
     * Sums all the values from a list that can be filtered optionally
     *
     * @param data A list of doubles
     * @param condition An optional condition that will filter the list
     */
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

    /**
     * Returns the average of a list that can be filtered
     *
     * @param data A list of values that will be cast into Double
     * @param condition An optional condition that will filter the list
     */
    fun average(data: List<String>){
        average(castData(data))
    }//todo: valjda ovde treba isto condition


    /**
     * Returns the average of a list that can be filtered
     *
     * @param data A list of values
     * @param condition An optional condition that will filter the list
     */
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
    // nije ovo dobro hahahah
    /**
     * Counts how many elements there are in a string list with or without a condition
     *
     * @param data A string list
     * @param condition An optional condition that will filter the list
     */
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