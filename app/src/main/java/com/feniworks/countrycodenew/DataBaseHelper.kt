package com.feniworks.countrycodenew

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class DataBaseHelper internal constructor(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DATABASE_VERSION) {
    private val db: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val inStream = context.assets.open("data.sql")
            BufferedReader(InputStreamReader(inStream)).forEachLine { line -> if (line.isNotBlank()) db.execSQL(line) }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    fun findCountry(countryId: String): ArrayList<String> {
        val data = ArrayList<String>()
        val cursor = db.rawQuery("SELECT * FROM countries WHERE id = $countryId", null)
        if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val code = cursor.getString(cursor.getColumnIndex("code"))
            val country = cursor.getString(cursor.getColumnIndex("name"))
            val capital = cursor.getString(cursor.getColumnIndex("capital"))
            val currency = cursor.getString(cursor.getColumnIndex("currency"))
            val timezone = cursor.getString(cursor.getColumnIndex("timezone"))
            val iso = cursor.getString(cursor.getColumnIndex("iso2"))

            data.add(id)
            data.add(code)
            data.add(country)
            data.add(capital)
            data.add(currency)
            data.add(timezone)
            data.add(iso)
        }
        cursor.close()
        return data
    }

    fun getList(orderByPrefix: Boolean): ArrayList<CountriesListItem> {
        val data = ArrayList<CountriesListItem>()
        val cursor = db.rawQuery("SELECT * FROM countries ORDER BY ${if (orderByPrefix) "code" else "name"} ASC", null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val prefix = cursor.getString(cursor.getColumnIndex("code"))
            val country = cursor.getString(cursor.getColumnIndex("name"))
            val timezone = cursor.getString(cursor.getColumnIndex("timezone"))
            val iso2 = cursor.getString(cursor.getColumnIndex("iso2"))
            val item = CountriesListItem(
                    id,
                    country,
                    prefix,
                    timezone,
                    iso2,
                    context
            )
            data.add(item)
        }
        cursor.close()
        return data
    }

    companion object {
        private const val DB_NAME = "data"
        private const val DATABASE_VERSION = 8
    }

}
