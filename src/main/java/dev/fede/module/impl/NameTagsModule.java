package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;

public class NameTagsModule extends Module {
   public final BooleanSetting players = this.addSetting(new BooleanSetting("Players", "Tag players with their IGN", true));
   public final BooleanSetting self = this.addSetting(new BooleanSetting("Self", "Also tag your own player (best in 3rd person)", false));
   public final BooleanSetting items = this.addSetting(new BooleanSetting("Items", "Tag dropped items on the ground", true));
   public final BooleanSetting hidePlayerTags = this.addSetting(
      new BooleanSetting("Hide Player Tags", "Remove the vanilla nametag of other players — only the module's shows", true)
   );
   public final BooleanSetting hideOwnTag = this.addSetting(
      new BooleanSetting("Hide Own Tag", "Remove your own original nametag — only the module's Self tag shows", true)
   );
   public final BooleanSetting hideOtherTags = this.addSetting(
      new BooleanSetting("Hide Other Tags", "Also remove vanilla nametags of mobs, armor stands & frames", false)
   );
   public final BooleanSetting armor = this.addSetting(new BooleanSetting("Armor", "Show a player's equipped armor above their tag", true));
   public final BooleanSetting heldItem = this.addSetting(new BooleanSetting("Held Item", "Show what a player is holding above their tag", true));
   public final BooleanSetting health = this.addSetting(new BooleanSetting("Health", "Health bar under player tags", true));
   public final BooleanSetting distance = this.addSetting(new BooleanSetting("Distance", "Append the distance in blocks", true));
   public final BooleanSetting itemAmount = this.addSetting(new BooleanSetting("Item Amount", "Show the stack size on item tags", true));
   public final SliderSetting scale = this.addSetting(new SliderSetting("Scale", "Tag size", 1.0, 0.5, 2.0, 0.1, "x"));
   public final SliderSetting opacity = this.addSetting(new SliderSetting("Opacity", "Tag transparency", 100.0, 10.0, 100.0, 5.0, "%"));
   public final SliderSetting range = this.addSetting(new SliderSetting("Range", "Only tag within this distance", 64.0, 8.0, 256.0, 4.0, "m"));

   public NameTagsModule() {
      super("NameTags", "HUD-styled nametags for players & items", Category.RENDER);
   }
}

