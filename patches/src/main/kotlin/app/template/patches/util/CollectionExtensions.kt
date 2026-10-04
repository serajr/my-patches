package app.template.patches.util

/**
 * Filtra a coleção com base em um critério e garante que reste exatamente 1 elemento.
 * Aceita o nome do recurso para relatórios de erro e o predicado de filtro.
 */
fun <T> Collection<T>.requireExactlyOne(
    resourceName: String, 
    predicate: (T) -> Boolean
): T {
    val filtered = this.filter(predicate)
    return when {
        filtered.isEmpty() -> throw IllegalStateException(
            "Erro de Engenharia Reversa: Nenhum elemento correspondeu ao critério para [$resourceName]."
        )
        filtered.size > 1 -> throw IllegalStateException(
            "Erro de Engenharia Reversa: Mais de um elemento (${filtered.size}) correspondeu ao critério para [$resourceName]."
        )
        else -> filtered.first()
    }
}
