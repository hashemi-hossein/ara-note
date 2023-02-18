package ara.note.domain.util

interface Mapper<T, R> {

    fun map(t: T): R

    fun mapList(list: List<T>): List<R> {
        return list.map { map(it) }
    }

    fun mapReverse(r: R): T {
        error("Not Implemented")
    }

    fun mapListReverse(list: List<R>): List<T> {
        return list.map { mapReverse(it) }
    }
}
