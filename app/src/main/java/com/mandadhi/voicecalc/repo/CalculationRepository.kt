package com.mandadhi.voicecalc.repo

import com.mandadhi.voicecalc.db.CalculationDao
import com.mandadhi.voicecalc.db.CalculationRecord
import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    fun all(): Flow<List<CalculationRecord>> = dao.all()
    suspend fun insert(record: CalculationRecord) = dao.insert(record)
    suspend fun clear() = dao.clearAll()
}
