package app.template.patches.util

fun <T> Collection<T>.requireExactlyOne(
    resourceName: String,
    predicate: (T) -> String
): T {
    if (this.size != 1) {
        throw IllegalStateException("Erro: Esperava 1 ocorrência para [$resourceName] mas achei ${this.size}")
    }
    return this.first()
}
