package com.feniworks.countrycodenew

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.feniworks.countrycodenew.databinding.ActivityMainBinding


@SuppressLint("SetTextI18n")
class MainActivity : Activity() {
    private var db: DataBaseHelper? = null
    private var adapter: CountriesListAdapter? = null

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DataBaseHelper(this)

        val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val taskDesc = ActivityManager.TaskDescription(
            getString(R.string.app_name),
            bm,
            resources.getColor(R.color.statusBar)
        )
        this.setTaskDescription(taskDesc)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.countriesList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val countryId =
                    (mainBinding.countriesList.getItemAtPosition(position) as CountriesListItem).id
                val iso2 =
                    (mainBinding.countriesList.getItemAtPosition(position) as CountriesListItem).iso2

                val data = db!!.findCountry(countryId)
                val builder = AlertDialog.Builder(this@MainActivity)
                // Get the layout inflater
                val inflater = this@MainActivity.layoutInflater
                val dialogView = inflater.inflate(R.layout.country_info, null)

                val phoneCodeTxt = dialogView.findViewById<TextView>(R.id.phoneCodeTxt)
                val countryTxt = dialogView.findViewById<TextView>(R.id.countryTxt)
                val capitalTxt = dialogView.findViewById<TextView>(R.id.capitalTxt)
                val currencyTxt = dialogView.findViewById<TextView>(R.id.currencyTxt)
                val timeZoneTxt = dialogView.findViewById<TextView>(R.id.timeZoneTxt)
                val iso2CodeTxt = dialogView.findViewById<TextView>(R.id.iso2CodeTxt)
                val flag = dialogView.findViewById<ImageView>(R.id.countryImg)

                phoneCodeTxt.text = "+" + data[1]
                countryTxt.text = data[2]
                capitalTxt.text = data[3]
                currencyTxt.text = data[4]
                timeZoneTxt.text = "UTC " + if (data[5] == "null") "N/A" else data[5]
                iso2CodeTxt.text = data[6]

                val imgID = resources.getIdentifier("flag_$iso2", "drawable", packageName)
                if (imgID != 0)
                    flag.setImageDrawable(resources.getDrawable(imgID))

                builder
                    .setCancelable(true)
                    .setView(dialogView)
                    .setPositiveButton("Close", null)
                    .create()
                    .show()
            }

        mainBinding.searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter?.filter!!.filter(mainBinding.searchView.text)
            }
        })

        val orderByPrefix = findViewById<Switch>(R.id.orderByPrefixSwitch)
        orderByPrefix.setOnCheckedChangeListener { _, b -> loadList(b) }

        loadList(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val msg = Html.fromHtml(
                    """
Developed by Feniworks<br>
Version ${BuildConfig.VERSION_NAME} (2025-11-11)<br><br>
If you like this application, please rate it with 5 stars in the Play Store :)<br><br>

Icons courtesy of <a href="https://flagpedia.net/">flagpedia.net</a><br><br>

Source code: <a href="https://github.com/feniworks/country-code">github.com/feniworks/country-code</a>
"""
                )
                val s = SpannableString(msg)
                Linkify.addLinks(s, Linkify.WEB_URLS);

                val alert = AlertDialog.Builder(this@MainActivity)
                    .setTitle("About Country Code")
                    .setMessage(s)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()

                alert.show()

                (alert.findViewById(android.R.id.message) as TextView).movementMethod =
                    LinkMovementMethod.getInstance()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun loadList(orderByPrefix: Boolean) {
        adapter = CountriesListAdapter(this, db!!.getList(orderByPrefix))
        adapter!!.filter.filter(mainBinding.searchView.text)
        mainBinding.countriesList.adapter = adapter
    }
}
