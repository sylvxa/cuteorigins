package lol.sylvie.cuteorigins.state.compat;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.operations.FileFixOperations;

public class CuteOriginsFileFix extends FileFix {
    public CuteOriginsFileFix(Schema schema) {
        super(schema);
    }

    @Override
    public void makeFixer() {
        addFileFixOperation(FileFixOperations.move("data/cuteorigins.dat", "data/cuteorigins/origins.dat"));
    }
}