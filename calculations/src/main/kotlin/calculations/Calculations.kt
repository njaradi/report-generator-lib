package calculations

import kotlin.collections.Map

class Calculations {

    // todo: napisati dokumentaciju

    private fun castStringToDouble (data: List<String>) : MutableList<Double>{
        try{
            return data.map { it.toDouble() }.toMutableList()
        }catch (e : Exception){
            throw Exception("could not cast list from String to Double")
        }
    }

    private fun castNestedStringToDouble (data: List<List<String>>) : List<List<Double>>{
        try{
            return data.map { innerList ->
                innerList.map { it.toDouble() }
            }
        }catch (e : Exception){
            throw Exception("could not cast list list from String to Double")
        }
    }

    private fun castNestedDoubleToString (data: List<List<Double>>) : List<List<String>>{
        try{
            return data.map { innerList ->
                innerList.map { it.toString() }
            }
        }catch (e : Exception){
            throw Exception("could not cast list list from Double to String")
        }
    }

    private fun castDoubleToSting (data: List<Double>) : MutableList<String>{
        try{
            return data.map { it.toString() }.toMutableList()
        }catch (e : Exception){
            throw Exception("could not cast list from Double to String")
        }
    }


    //column
    /**
     * Subtracts values of string list from a string list, by taking each
     * element and subtracting each other, finally it returns the resulting list
     *
     * @param minuend a list that represents the first element in a subtraction
     * @param subtrahend a list that represents the second element in a subtraction
     */
    fun subString(minuend: List<String>, subtrahend: List<String>): List<String> {
        val difference: List<Double> = sub(castStringToDouble(minuend),castStringToDouble(subtrahend))
        return castDoubleToSting(difference).toList()
    }

    /**
     * Subtracts values of a list from a list, by taking each
     * element and subtracting each other, finally it returns the resulting list
     *
     * @param minuend a list that represents the first element in a subtraction
     * @param subtrahend a list that represents the second element in a subtraction
     */
    fun sub(minuend: List<Double>, subtrahend: List<Double>): List<Double> {
        val difference: MutableList<Double> = minuend.toMutableList()
        for(i in minuend.indices)
        {
             difference[i] = minuend[i]-subtrahend[i]
        }
        return difference.toList()
    }

    /**
     * Multiples values of multiple string lists with each other, by taking each
     * element and multiplying with the equivalent elements of the other lists,
     * finally it returns the resulting list
     *
     * @param multipliers a list of lists that will be multiplied with each other
     */
    fun multiplyString(multipliers: List<List<String>>) : List<String>{
        return castDoubleToSting(multiply(castNestedStringToDouble(multipliers)))
    }

    /**
     * Multiples values of multiple lists with each other, by taking each
     * element and multiplying with the equivalent elements of the other lists,
     * finally it returns the resulting list
     *
     * @param multipliers a list that represents the first element in a subtraction
     */
    fun multiply(multipliers: List<List<Double>>) : List<Double>{
        val product = MutableList(multipliers[0].size) { 1.0 }
        for ((i, multiplier) in multipliers.withIndex()) {
            product[i] *= multiplier[i]
        }
        return product.toList()
    }

    fun divideString(dividend: List<String>, divisor: List<String>): List<String> {
        val quotient: List<Double> = divide(castStringToDouble(dividend),castStringToDouble(divisor))
        return castDoubleToSting(quotient).toList()
    }

    fun divide(dividend: List<Double>, divisor: List<Double>): List<Double> {
        val quotient: MutableList<Double> = dividend.toMutableList()
        for(i in dividend.indices)
        {
            quotient[i] = dividend[i]/divisor[i]
        }
        return quotient.toList()
    }

    fun sumString(addends: List<List<String>>) : List<String>{
        return castDoubleToSting(sum(castNestedStringToDouble(addends)))
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

    private fun conditionString(data: List<String>, condition : String) : List<String>
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
    fun sumString(data: List<String>, condition : String? = null) : Double{
        return sum(castStringToDouble(data), condition)
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
    fun averageString(data: List<String>, condition: String? = null): Double {
        return average(castStringToDouble(data), condition)
    }


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
                return conditionString(data,condition).size
            }else{
                return condition(castStringToDouble(data), condition).size
            }
        }
        return data.size
    }

}