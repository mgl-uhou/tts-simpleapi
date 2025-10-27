package com.mgl_uhou.service

import com.mgl_uhou.exceptions.LimitExceededException
import com.mgl_uhou.plugins.CharacterCounter
import org.jetbrains.exposed.sql.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

private data class CounterState(val usedChars: Long, val lastUpdate: LocalDate)

class CharacterCounterService {
    private val counterId = 1
    private val monthlyCharLimit = 1_000_000L

    /**
     * Busca o estado atual do contador no banco de dados.
     * @return O estado atual do contador.
     * @throws IllegalStateException se a linha do contador não for encontrada.
     */
    private suspend fun getCounterState(): CounterState {
        return newSuspendedTransaction(Dispatchers.IO) {
            CharacterCounter.selectAll().where { CharacterCounter.id eq counterId }
                .firstOrNull()
                ?.let {
                    CounterState(
                        usedChars = it[CharacterCounter.usedChars],
                        lastUpdate = it[CharacterCounter.lastUpdate]
                    )
                } 
                ?: throw IllegalStateException("Counter not found. ID: $counterId")
        }
    }

    /**
     * Valida a contagem de caracteres e atualiza o banco de dados.
     * Lança uma exceção se o limite for excedido.
     */
    suspend fun validateAndIncrement(charsToAdd: Int) {
        val currentState = getCounterState()
        val now = LocalDate.now()

        val newCharCount: Long

        if (now.monthValue > currentState.lastUpdate.monthValue || now.year > currentState.lastUpdate.year) {
            newCharCount = charsToAdd.toLong()
        } else {
            if (currentState.usedChars + charsToAdd > monthlyCharLimit) {
                throw LimitExceededException("Monthly character limit of 1,000,000 exceeded.")
            }
            newCharCount = currentState.usedChars + charsToAdd
        }

        newSuspendedTransaction(Dispatchers.IO) {
            CharacterCounter.update({ CharacterCounter.id eq counterId }) {
                it[usedChars] = newCharCount
                it[lastUpdate] = now
            }
        }
    }
}