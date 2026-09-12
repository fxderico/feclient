package dev.fede.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class BlockListSetting extends Setting<List<BlockListSetting.Target>> {
   public static final int DEFAULT_COLOR = -16711736;
   private final Set<Identifier> ids = new HashSet<>();

   public BlockListSetting(String name, String description) {
      super(name, description, new ArrayList<>());
   }

   public List<BlockListSetting.Target> targets() {
      return this.value;
   }

   public int size() {
      return this.value.size();
   }

   public long enabledCount() {
      return this.value.stream().filter(t -> t.enabled.get()).count();
   }

   public boolean contains(Identifier id) {
      return this.ids.contains(id);
   }

   public BlockListSetting.Target find(Block block) {
      if (block == null) {
         return null;
      } else {
         Identifier id = Registries.BLOCK.getId(block);
         if (id != null && this.ids.contains(id)) {
            for (BlockListSetting.Target t : this.value) {
               if (t.id().equals(id)) {
                  return t;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean isActive(Block block) {
      BlockListSetting.Target t = this.find(block);
      return t != null && t.enabled.get();
   }

   public BlockListSetting.Target add(Block block, boolean enabled, int color) {
      if (block != null && block != Blocks.AIR) {
         Identifier id = Registries.BLOCK.getId(block);
         if (id != null && !this.ids.contains(id)) {
            BlockListSetting.Target target = new Target(id, block, enabled, color);
            this.value.add(target);
            this.ids.add(id);
            return target;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void remove(BlockListSetting.Target target) {
      if (this.value.remove(target)) {
         this.ids.remove(target.id());
      }
   }

   public void clear() {
      this.value.clear();
      this.ids.clear();
   }

   public List<Block> searchRegistry(String rawQuery, int limit) {
      String q = rawQuery == null ? "" : rawQuery.trim().toLowerCase(Locale.ROOT);
      List<Block> out = new ArrayList<>();
      if (!q.isEmpty() && limit > 0) {
         for (Block block : Registries.BLOCK) {
            if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
               Identifier id = Registries.BLOCK.getId(block);
               if (id != null && !this.ids.contains(id)) {
                  String path = id.getPath().toLowerCase(Locale.ROOT);
                  String ns = id.getNamespace().toLowerCase(Locale.ROOT);
                  String name = displayName(block).toLowerCase(Locale.ROOT);
                  if (path.contains(q) || ns.contains(q) || name.contains(q)) {
                     out.add(block);
                     if (out.size() >= limit) {
                        break;
                     }
                  }
               }
            }
         }

         return out;
      } else {
         return out;
      }
   }

   public static String displayName(Block block) {
      try {
         return block.getName().getString();
      } catch (Throwable var3) {
         Identifier id = Registries.BLOCK.getId(block);
         return id != null ? id.getPath() : "block";
      }
   }

   public void seedDefaults() {
      this.clear();
      this.add(Blocks.DIAMOND_ORE, true, -16711736);
      this.add(Blocks.DEEPSLATE_DIAMOND_ORE, true, -16711736);
      this.add(Blocks.EMERALD_ORE, false, -16711868);
      this.add(Blocks.DEEPSLATE_EMERALD_ORE, false, -16711868);
      this.add(Blocks.ANCIENT_DEBRIS, true, -39356);
      this.add(Blocks.NETHER_GOLD_ORE, false, -10496);
      this.add(Blocks.GOLD_ORE, false, -10496);
      this.add(Blocks.DEEPSLATE_GOLD_ORE, false, -10496);
      this.add(Blocks.IRON_ORE, false, -3618616);
      this.add(Blocks.DEEPSLATE_IRON_ORE, false, -3618616);
      this.add(Blocks.COAL_ORE, false, -12303292);
      this.add(Blocks.DEEPSLATE_COAL_ORE, false, -12303292);
      this.add(Blocks.COPPER_ORE, false, -4689101);
      this.add(Blocks.DEEPSLATE_COPPER_ORE, false, -4689101);
      this.add(Blocks.LAPIS_ORE, false, -12490271);
      this.add(Blocks.DEEPSLATE_LAPIS_ORE, false, -12490271);
      this.add(Blocks.REDSTONE_ORE, false, -65536);
      this.add(Blocks.DEEPSLATE_REDSTONE_ORE, false, -65536);
      this.add(Blocks.SPAWNER, false, -7846657);
      this.add(Blocks.END_PORTAL_FRAME, false, -12255250);
      this.add(Blocks.CHEST, false, -22016);
   }

   @Override
   public JsonElement toJson() {
      JsonArray arr = new JsonArray();

      for (BlockListSetting.Target t : this.value) {
         JsonObject o = new JsonObject();
         o.addProperty("id", t.id().toString());
         o.addProperty("enabled", t.enabled.get());
         o.addProperty("color", t.color.get());
         arr.add(o);
      }

      return arr;
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonArray()) {
         this.clear();
         Iterator var2 = element.getAsJsonArray().iterator();

         while (true) {
            JsonObject o;
            Identifier id;
            while (true) {
               if (!var2.hasNext()) {
                  return;
               }

               JsonElement el = (JsonElement)var2.next();
               if (el.isJsonObject()) {
                  o = el.getAsJsonObject();
                  if (o.has("id")) {
                     try {
                        id = Identifier.of(o.get("id").getAsString());
                        break;
                     } catch (Exception var9) {
                     }
                  }
               }
            }

            Block block = (Block)Registries.BLOCK.get(id);
            if (block != null && block != Blocks.AIR) {
               boolean en = !o.has("enabled") || o.get("enabled").getAsBoolean();
               int color = o.has("color") ? o.get("color").getAsInt() : -16711736;
               this.add(block, en, color);
            }
         }
      }
   }

   public final class Target {
      private Identifier id;
      private Block block;
      public BooleanSetting enabled;
      public ColorSetting color;

      Target(Identifier id, Block block, boolean enabled, int color) {
         this.id = id;
         this.block = block;
         this.enabled = new BooleanSetting("Enabled", "Highlight this block", enabled);
         this.color = new ColorSetting(BlockListSetting.displayName(block), "Highlight color", color);
      }

      public Identifier id() {
         return this.id;
      }

      public Block block() {
         return this.block;
      }

      public String label() {
         return this.color.getName();
      }
   }
}

