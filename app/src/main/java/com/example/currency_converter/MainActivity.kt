package com.example.currency_converter

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import khttp.responses.Response
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.spinnerFrom
import org.json.JSONObject
import java.lang.Exception
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private var second: String = "CAD"
    private var first: String = "CAD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, "Please do not enter incorrect values", Toast.LENGTH_LONG).show()

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.values_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                first = spinnerFrom.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                second = spinnerTo.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        buttonConv.setOnClickListener {
            val task = CurrencyTask(this)
            task.execute()
        }
    }

    class CurrencyTask constructor(private var activity: MainActivity) :
        AsyncTask<Void, Void, String>() {
        private var str = ""
        override fun onPreExecute() {
            super.onPreExecute()
            val input: EditText = activity.findViewById(R.id.input)
            str = input.text.toString()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                val response: Response = khttp.get(
                    url = "https://api.exchangeratesapi.io/latest",
                    params = mapOf(
                        "symbols" to activity.second,
                        "base" to activity.first
                    )
                )
                val obj: JSONObject = response.jsonObject
                val nameValue: JSONObject = obj.getJSONObject("rates")
                val valueStr: String = nameValue.getString(activity.second)
                val rate: Double = valueStr.toDouble()
                val inputValue: String = str
                val result: Double = round(inputValue.toDouble() * rate * 100) / 100
                return "Exchange rate " + activity.first + " to " + activity.second + " = " +
                        "\n" + valueStr + "\n" + "\n" + "Result: " + result.toString()
            } catch (ex: Exception) {
                return "Can not convert"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val textResult: TextView = activity.findViewById(R.id.textResult)
            textResult.text = result
        }
    }
}
