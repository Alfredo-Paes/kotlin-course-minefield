package model

import java.util.*

enum class BoardEvent { VICTORY, DEFEAT };

class Board(val numberOfLines: Int, val numberOfColumns: Int, private val numberOfMines: Int) {
    private val fields = ArrayList<ArrayList<Field>>();
    private val callbacks = ArrayList<(BoardEvent) -> Unit>();

    init {
        generateFields();
        associateNeighbors();
        sortMines();
    }

    private fun generateFields() {
        for (line in 0 until numberOfLines) {
            fields.add(ArrayList());
            for (column in 0 until numberOfColumns) {
                val newField = Field(line, column);
                newField.onEventField(this::verifyDefeatOrVictory);
                fields[line].add(newField);
            }
        }
    }

    private fun associateNeighbors() {
        forEachFields { associateNeighbors(it) }
    }

    private fun associateNeighbors(field: Field) {
        val (line, column) = field;
        val lines = arrayOf(line - 1, line, line + 1);
        val columns = arrayOf(column - 1, column, column + 1);

        lines.forEach { line ->
            columns.forEach { column ->
                val current = fields.getOrNull(line)?.getOrNull(column);
                current?.takeIf { field != it }?.let { field.addNeighbor(it) }
            }
        }
    }

    private fun sortMines() {
        val generate = Random();

        var drawnLine = -1;
        var drawnColumn = -1;
        var numberOfMinesCurrent = 0;

        while (numberOfMinesCurrent < this.numberOfMines) {
            drawnLine = generate.nextInt(numberOfLines);
            drawnColumn = generate.nextInt(numberOfColumns);

            val drawnField = fields[drawnLine][drawnColumn];
            if (drawnField.safe) {
                drawnField.miner();
                numberOfMinesCurrent++;
            }
        }
    }

    private fun goalAchieved(): Boolean {
        var playerWon = true;
        forEachFields { if (!it.goalAchievied) playerWon = false };
        return playerWon;
    }

    private fun verifyDefeatOrVictory(field: Field, event: FieldEvent) {
        if (event == FieldEvent.EXPLOSION) {
            callbacks.forEach { it(BoardEvent.DEFEAT) }
        } else if (goalAchieved()) {
            callbacks.forEach { it(BoardEvent.VICTORY) }
        }
    }

    fun forEachFields(callback: (Field) -> Unit) {
        fields.forEach { line -> line.forEach(callback) }
    }

    fun onEventBoard(callback: (BoardEvent) -> Unit) {
        callbacks.add(callback);
    }

    fun reset() {
        forEachFields { it.reset() };
        sortMines();
    }
}