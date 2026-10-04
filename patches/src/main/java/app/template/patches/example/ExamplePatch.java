package app.template.patches.example;

import app.morphe.patcher.patch.Patch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import app.template.patches.shared.ConstantsJava; // Altere para Constants se seu arquivo chamar Constants.java
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import app.morphe.patcher.patch.Compatibility;

public class ExamplePatch extends Patch {

    private final List<Compatibility> compatibility = new ArrayList<>();
    private final List<String> extensions = new ArrayList<>();
    private final List<String> dependencies = new ArrayList<>();

    public ExamplePatch() {
        setName("Example Patch");
        setDescription("Exemplo de patch funcional escrito em Java.");
        
        // Define o Internal Patch como dependência obrigatória
        getDependencies().add("Internal Patch");
        
        // Vincula compatibilidades e extensões
        getCompatibility().add(ConstantsJava.COMPATIBILITY_EXAMPLE); // Altere para Constants se necessário
        getExtensions().add("extensions/extension.mpe");
    }

    @NotNull
    @Override
    public List<Compatibility> getCompatibility() {
        return compatibility;
    }

    @NotNull
    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @NotNull
    @Override
    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public void execute(@NotNull PatchContext context) {
        InstructionExtensions.addInstructions(
            Fingerprints.INSTANCE.getMethod(),
            0,
            "invoke-static {}, Lapp/template/extension/ExamplePatch;->showAds()Z\nmove-result v0\nreturn v0"
        );
    }
}
