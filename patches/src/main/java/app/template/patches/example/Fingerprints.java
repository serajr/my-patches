package app.template.patches.example;

import app.morphe.patcher.Fingerprint;
import app.morphe.patcher.InstructionLocation;
import app.morphe.patcher.InstructionFilterKt;
import com.android.tools.smali.dexlib2.AccessFlags;
import com.android.tools.smali.dexlib2.Opcode;
import java.util.Arrays;

/**
 * See:
 * https://github.com/MorpheApp/morphe-patcher/blob/main/docs
 * https://github.com/MorpheApp/morphe-patcher/blob/main/docs/2_2_1_fingerprinting.md
 */
public class AdLoaderFingerprint extends Fingerprint {

    // Instancia o padrão Singleton estático para ser consumido pelo ExamplePatch
    public static final AdLoaderFingerprint INSTANCE = new AdLoaderFingerprint();

    private AdLoaderFingerprint() {
        super(
            // Classe definidora (Mapeamento flexível por StringComparisonType)
            "Lcom/some/app/ads/AdsLoader;",
            
            // Nome exato do método alvo
            "showAds",
            
            // Flags de acesso obrigatórias
            Arrays.asList(AccessFlags.PUBLIC, AccessFlags.FINAL),
            
            // Tipo de retorno esperado (Z = boolean)
            "Z",
            
            // Lista de parâmetros exatos ou tipos ocultados ("L")
            Arrays.asList("Ljava/lang/String;", "I", "L"),
            
            // Lista de filtros de instrução em ordem cronológica de bytecode
            Arrays.asList(
                // Filtro 1: Acesso a campo interno (IGET)
                InstructionFilterKt.fieldAccess(
                    Opcode.IGET,
                    "this",
                    null,
                    "Ljava/util/Map;",
                    null,
                    null,
                    new InstructionLocation.MatchAfter()
                ),

                // Filtro 2: Constante de texto
                InstructionFilterKt.string(
                    "showBannerAds",
                    null,
                    new InstructionLocation.MatchAfter()
                ),

                // Filtro 3: Chamada de método (String.equals)
                InstructionFilterKt.methodCall(
                    null,
                    "Ljava/lang/String;",
                    "equals",
                    null,
                    null,
                    null,
                    null,
                    new InstructionLocation.MatchAfter()
                ),

                // Filtro 4: MOVE_RESULT imediatamente colado após a última instrução válida
                InstructionFilterKt.opcode(
                    Opcode.MOVE_RESULT,
                    new InstructionLocation.MatchAfterImmediately()
                ),

                // Filtro 5: Valor numérico literal
                InstructionFilterKt.literal(
                    1337L,
                    null,
                    new InstructionLocation.MatchAfter()
                ),

                // Filtro 6: Condicional IF_EQ
                InstructionFilterKt.opcode(
                    Opcode.IF_EQ,
                    new InstructionLocation.MatchAfter()
                )
            ),
            
            // Parâmetros estruturais adicionais herdados da assinatura base do construtor
            null,
            null
        );
    }
}
