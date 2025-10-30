package com.mandadhi.voicecalc.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class CalculationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inputText: String,
    val parsedExpression: String,
    val resultText: String,
    val timestamp: Long = System.currentTimeMillis()
)
