package model

enum class FieldEvent { OPENING, MARKING, UNMARKING, EXPLOSION, RESET }

data class Field(val line: Int, val column: Int) {
    private val neighbors = ArrayList<Field>();
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>();

    var marked: Boolean = false;
    var open: Boolean = false;
    var mined: Boolean = false;

    // Only read
    val unmarked: Boolean get() = !marked;
    val closed: Boolean get() = !open;
    val safe: Boolean get() = !mined;
    val goalAchievied: Boolean get() = safe && open || mined && marked;
    val numberOfMinedNeighbors: Int get() = neighbors.filter { it.mined }.size;
    val safeNeighborhood: Boolean
        get() = neighbors.map { it.safe }.reduce { result, safe -> result && safe };

    fun addNeighbor(neighbor: Field) {
        neighbors.add(neighbor);
    }

    fun onEventField(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback);
    }

    fun open() {
        if (closed) {
            open = true;
            if (mined) {
                callbacks.forEach { it(this, FieldEvent.EXPLOSION) };
            } else {
                callbacks.forEach { it(this, FieldEvent.OPENING) };
                neighbors.filter { it.closed && it.safe && safeNeighborhood }.forEach { it.open() }
            }
        }
    }

    fun miner() {
        mined = true;
    }

    fun reset() {
        open = false;
        mined = false;
        marked = false;
        callbacks.forEach { it(this, FieldEvent.RESET) };
    }
}