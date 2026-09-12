package dev.fede.zenithgui.utility.render.display.base;

// [lombok removed]
import net.minecraft.util.Identifier;
import dev.fede.FeClient;
import dev.fede.zenithgui.Zenith;

public class CustomSprite {
    private final Identifier texture;

    public CustomSprite(String path) {
        if (path.contains(":")) {
            this.texture = Identifier.of(path);
        } else if (path.contains("/")) {
            this.texture = Zenith.id(path);
        } else {
            this.texture = Zenith.id("icons/category/" + path);
        }
    }

        public Identifier getTexture() {
        return this.texture;
    }
}
