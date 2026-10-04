package app.template.patches.util

/**
 * Garante que uma coleção possui exatamente um elemento.
 * Caso contrário, lança uma exceção descritiva com o nome do recurso pesquisado.
 */
fun <T> Collection<T>.requireExactlyOne(resourceName: String): T {
    if (this.size != 1) {
        throw IllegalStateException(
            "Erro de Engenharia Reversa: Esperava exatamente 1 ocorrência para [$resourceName], " +
            "mas foram encontradas ${this.size} correspondências no bytecode."
        )
    }
    return this.first()
}
