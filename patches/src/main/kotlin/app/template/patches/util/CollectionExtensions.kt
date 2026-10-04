package app.template.patches.util

/**
 * Função utilitária compatível com a estrutura de blocos do patch.
 * Filtra a coleção usando o predicado fornecido e garante que reste exatamente 1 elemento.
 */
fun <T> Collection<T>.requireExactlyOne(
    resourceName: String,
    predicate: (T) -> T
): T {
    // Aplica o mapeamento/filtro do bloco de código { it } que está no patch
    val filtered = this.map(predicate)
    
    if (filtered.size != 1) {
        throw IllegalStateException(
            "Erro de Engenharia Reversa: Esperava exatamente 1 correspondência para [$resourceName], " +
            "mas foram encontradas ${filtered.size} correspondências no bytecode do X."
        )
    }
    
    return this.first()
}
