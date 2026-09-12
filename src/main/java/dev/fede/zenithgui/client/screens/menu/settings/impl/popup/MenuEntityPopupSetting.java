package dev.fede.zenithgui.client.screens.menu.settings.impl.popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
// [lombok removed]
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
// [unsupported setting type removed]
import dev.fede.zenithgui.adapter.EntitySelectSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuPopupSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;
import dev.fede.zenithgui.utility.math.MathUtil;
import dev.fede.zenithgui.utility.render.display.ScrollHandler;
import dev.fede.zenithgui.utility.render.display.TextBox;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.ChangeRect;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuEntityPopupSetting extends MenuPopupSetting implements IMinecraft {
    private final EntitySelectSetting setting;
    private final TextBox searchBox;
    private final ScrollHandler scrollHandler = new ScrollHandler();
    private boolean rebornSort = false;
    private final Map<EntityType<?>, Rect> entityBounds = new HashMap<>();

    public MenuEntityPopupSetting(EntitySelectSetting setting, ChangeRect bounds) {
        super(bounds);
        this.searchBox = new TextBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(7.0F), "Search...", 78.0F);
        this.animationScale.update(1.0F);
        this.setting = setting;
    }

    @Override
    public void render(UIContext ctx, float mouseX, float mouseY, float alphas, Theme theme) {
        this.animationScale.update();
        alphas = 1.0F;
        float x = this.bounds.getX();
        float y = this.bounds.getY();
        float width = this.bounds.getWidth();
        float height = this.bounds.getHeight() - 20.0F - 4.0F;
        // pushMatrix/translate/scale removed: no-ops in MC 1.21.1
        ctx.drawRoundedRect(
            this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), height, BorderRadius.all(4.0F), theme.getForegroundColor().mulAlpha(alphas)
        );
        ctx.drawRoundedRect(
            this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), 18.0F, BorderRadius.top(4.0F, 4.0F), theme.getForegroundLight().mulAlpha(alphas)
        );
        Font entityFont = Fonts.MEDIUM.getFont(7.0F);
        Font iconFont = Fonts.ICONS.getFont(7.0F);
        ctx.drawText(entityFont, this.setting.getName(), x + 8.0F + 11.2F + 3.0F, y + 7.55F, theme.getWhite());
        ctx.drawText(Fonts.ICONS.getFont(8.0F), "E", x + 8.0F, y + 6.0F, theme.getWhiteGray());
        float sortSize = 14.0F;
        ctx.drawRoundedRect(x + width - sortSize - 8.0F, y + 3.0F, sortSize, sortSize, BorderRadius.all(2.0F), theme.getForegroundGray().mulAlpha(alphas));
        ctx.drawText(iconFont, "W", x + width - 8.0F - sortSize + (sortSize - iconFont.width("W")) / 2.0F + 1.0F, y + 6.6F, theme.getColor());
        List<EntityType<?>> sortedList = this.searchBox.isEmpty() && this.rebornSort ? getAllEntities().toList() : getAllEntities().sorted((o1, o2) -> {
            if (this.searchBox.isEmpty()) {
                boolean containsInSetting1 = this.setting.contains((EntityType<?>)o1);
                boolean containsInSetting2 = this.setting.contains((EntityType<?>)o2);
                return Boolean.compare(!containsInSetting1, !containsInSetting2);
            } else {
                String query = this.searchBox.getText().toLowerCase().trim();
                String name1 = o1.getTranslationKey().replaceFirst("^entity\\.minecraft\\.", "").replaceAll("_", " ");
                String name2 = o2.getTranslationKey().replaceFirst("^entity\\.minecraft\\.", "").replaceAll("_", " ");
                boolean matchesSearch1 = name1.toLowerCase().contains(query);
                boolean matchesSearch2 = name2.toLowerCase().contains(query);
                return Boolean.compare(!matchesSearch1, !matchesSearch2);
            }
        }).toList();
        float contentHeight = (float)sortedList.size() * 20.0F;
        this.scrollHandler.setMax((double)Math.max(0.0F, contentHeight - height));
        this.scrollHandler.update();
        int padding = 4;
        float itemY = (float)(padding + 18) + y - (float)this.scrollHandler.getValue();
        float itemX = x;
        float itemWidth = width;
        ColorRGBA textColor = theme.getWhite().mulAlpha(alphas);
        ColorRGBA bgColor = theme.getColor().mulAlpha(alphas);
        this.entityBounds.clear();
        ColorRGBA graySlotColor = theme.getForegroundColor();
        ColorRGBA themeSlotColor = theme.getForegroundLight();
        ctx.enableScissor((int)x, (int)y + 18 + padding, (int)(x + width), (int)(y + height - (float)padding));
        int i = 0;

        for (EntityType<?> entity : sortedList) {
            i++;
            if (itemY < y) {
                itemY += 20.0F;
            } else {
                boolean selected = this.setting.contains(entity);
                Rect rect = new Rect(itemX, itemY, itemWidth, 20.0F);
                ctx.drawRoundedRect(
                    rect.x(), rect.y(), rect.width(), rect.height(), BorderRadius.ZERO, selected ? bgColor : (i % 2 == 0 ? graySlotColor : themeSlotColor)
                );
                this.entityBounds.put(entity, rect);
                String name = entity.getTranslationKey().replaceFirst("^entity\\.minecraft\\.", "").replaceAll("_", " ");
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                try {
                    if (entity == EntityType.PLAYER) {
                        ctx.drawItem(Items.PLAYER_HEAD.getDefaultStack(), (int)(itemX + 8.0F), (int)(itemY + 4.4F));
                    } else {
                        Item spawnEgg = this.getEntitySpawnEgg(entity);
                        if (spawnEgg != null) {
                            ctx.drawItem(spawnEgg.getDefaultStack(), (int)(itemX + 8.0F), (int)(itemY + 4.4F));
                        } else {
                            String icon = this.getEntityIcon(entity);
                            ctx.drawText(Fonts.ICONS.getFont(8.0F), icon, itemX + 8.0F, itemY + 6.0F, theme.getWhiteGray());
                        }
                    }
                } catch (Exception var31) {
                    String icon = this.getEntityIcon(entity);
                    ctx.drawText(Fonts.ICONS.getFont(8.0F), icon, itemX + 8.0F, itemY + 6.0F, theme.getWhiteGray());
                }
                ctx.drawText(Fonts.BOLD.getFont(8.0F), ".", itemX + 8.0F + 11.2F + 3.0F, itemY + 5.0F, theme.getWhiteGray());
                ctx.drawText(entityFont, name, itemX + 8.0F + 11.2F + 8.0F, itemY + 7.55F, selected ? textColor : theme.getGrayLight());
                itemY += 20.0F;
                if (itemY > y + height) {
                    break;
                }
            }
        }

        ctx.disableScissor();
        ctx.enableScissor((int)x, (int)(y + height + 4.0F), (int)(x + width), (int)(y + height + 24.0F));
        ctx.drawRoundedRect(x, y + height + 4.0F, width, 20.0F, BorderRadius.all(4.0F), theme.getForegroundColor().mulAlpha(alphas));
        this.searchBox.setWidth(width - 20.0F);
        this.searchBox.render(ctx, x + 8.0F, y + height + 4.0F + 8.0F, theme.getWhite().mulAlpha(alphas), theme.getGray().mulAlpha(alphas));
        this.searchBox.setMaxLength(35);
        ctx.disableScissor();
        ctx.popMatrix();
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        this.searchBox.onMouseClicked(mouseX, mouseY, button);
        float x = this.bounds.getX();
        float y = this.bounds.getY();
        float width = this.bounds.getWidth();
        float height = this.bounds.getHeight();
        if (mouseY > (double)(y + 18.0F)) {
            for (Entry<EntityType<?>, Rect> entry : this.entityBounds.entrySet()) {
                if (entry.getValue().contains(mouseX, mouseY)) {
                    if (this.setting.contains(entry.getKey())) {
                        this.setting.remove(entry.getKey());
                    } else {
                        this.setting.add(entry.getKey());
                    }

                    return;
                }
            }
        }

        if (MathUtil.isHovered(mouseX, mouseY, (double)(x + width - 8.0F - 16.0F), (double)(y + 3.0F), 16.0, 16.0)) {
            this.rebornSort = !this.rebornSort;
            this.scrollHandler.setTargetValue(0.0);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.searchBox.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrollHandler.scroll(verticalAmount);
        return true;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public float getHeight() {
        return 0.0F;
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    private Identifier getEntityTexture(EntityType<?> entity) {
        if (entity == EntityType.PLAYER) {
            return Identifier.of("textures/entity/steve.png");
        } else if (entity == EntityType.ZOMBIE) {
            return Identifier.of("textures/entity/zombie/zombie.png");
        } else if (entity == EntityType.HUSK) {
            return Identifier.of("textures/entity/zombie/husk.png");
        } else if (entity == EntityType.DROWNED) {
            return Identifier.of("textures/entity/zombie/drowned.png");
        } else if (entity == EntityType.SKELETON) {
            return Identifier.of("textures/entity/skeleton/skeleton.png");
        } else if (entity == EntityType.STRAY) {
            return Identifier.of("textures/entity/skeleton/stray.png");
        } else if (entity == EntityType.CREEPER) {
            return Identifier.of("textures/entity/creeper/creeper.png");
        } else if (entity == EntityType.ENDERMAN) {
            return Identifier.of("textures/entity/enderman/enderman.png");
        } else if (entity == EntityType.SPIDER) {
            return Identifier.of("textures/entity/spider/spider.png");
        } else if (entity == EntityType.CAVE_SPIDER) {
            return Identifier.of("textures/entity/spider/cave_spider.png");
        } else if (entity == EntityType.WITCH) {
            return Identifier.of("textures/entity/witch.png");
        } else if (entity == EntityType.PILLAGER) {
            return Identifier.of("textures/entity/illager/pillager.png");
        } else if (entity == EntityType.VINDICATOR) {
            return Identifier.of("textures/entity/illager/vindicator.png");
        } else if (entity == EntityType.EVOKER) {
            return Identifier.of("textures/entity/illager/evoker.png");
        } else if (entity == EntityType.ENDER_DRAGON) {
            return Identifier.of("textures/entity/enderdragon/dragon.png");
        } else if (entity == EntityType.WITHER) {
            return Identifier.of("textures/entity/wither/wither.png");
        } else if (entity == EntityType.GHAST) {
            return Identifier.of("textures/entity/ghast/ghast.png");
        } else if (entity == EntityType.BLAZE) {
            return Identifier.of("textures/entity/blaze.png");
        } else if (entity == EntityType.SLIME) {
            return Identifier.of("textures/entity/slime/slime.png");
        } else if (entity == EntityType.MAGMA_CUBE) {
            return Identifier.of("textures/entity/slime/magma_cube.png");
        } else if (entity == EntityType.IRON_GOLEM) {
            return Identifier.of("textures/entity/iron_golem.png");
        } else if (entity == EntityType.SNOW_GOLEM) {
            return Identifier.of("textures/entity/snow_golem.png");
        } else if (entity == EntityType.VILLAGER) {
            return Identifier.of("textures/entity/villager/villager.png");
        } else if (entity == EntityType.WANDERING_TRADER) {
            return Identifier.of("textures/entity/wandering_trader.png");
        } else if (entity == EntityType.PIG) {
            return Identifier.of("textures/entity/pig/pig.png");
        } else if (entity == EntityType.COW) {
            return Identifier.of("textures/entity/cow/cow.png");
        } else if (entity == EntityType.SHEEP) {
            return Identifier.of("textures/entity/sheep/sheep.png");
        } else if (entity == EntityType.CHICKEN) {
            return Identifier.of("textures/entity/chicken.png");
        } else if (entity == EntityType.HORSE) {
            return Identifier.of("textures/entity/horse/horse_white.png");
        } else if (entity == EntityType.DONKEY) {
            return Identifier.of("textures/entity/horse/donkey.png");
        } else if (entity == EntityType.MULE) {
            return Identifier.of("textures/entity/horse/mule.png");
        } else if (entity == EntityType.LLAMA) {
            return Identifier.of("textures/entity/llama/llama_creamy.png");
        } else if (entity == EntityType.WOLF) {
            return Identifier.of("textures/entity/wolf/wolf.png");
        } else if (entity == EntityType.CAT) {
            return Identifier.of("textures/entity/cat/cat_black.png");
        } else if (entity == EntityType.OCELOT) {
            return Identifier.of("textures/entity/cat/ocelot.png");
        } else if (entity == EntityType.BAT) {
            return Identifier.of("textures/entity/bat.png");
        } else if (entity == EntityType.SQUID) {
            return Identifier.of("textures/entity/squid.png");
        } else if (entity == EntityType.GLOW_SQUID) {
            return Identifier.of("textures/entity/squid/glow_squid.png");
        } else if (entity == EntityType.DOLPHIN) {
            return Identifier.of("textures/entity/dolphin.png");
        } else if (entity == EntityType.TURTLE) {
            return Identifier.of("textures/entity/turtle/big_sea_turtle.png");
        } else if (entity == EntityType.COD) {
            return Identifier.of("textures/entity/fish/cod.png");
        } else if (entity == EntityType.SALMON) {
            return Identifier.of("textures/entity/fish/salmon.png");
        } else if (entity == EntityType.PUFFERFISH) {
            return Identifier.of("textures/entity/fish/pufferfish.png");
        } else {
            return entity == EntityType.TROPICAL_FISH ? Identifier.of("textures/entity/fish/tropical_fish.png") : null;
        }
    }

    private Item getEntitySpawnEgg(EntityType<?> entityType) {
        if (entityType == EntityType.ZOMBIE) {
            return Items.ZOMBIE_SPAWN_EGG;
        } else if (entityType == EntityType.SKELETON) {
            return Items.SKELETON_SPAWN_EGG;
        } else if (entityType == EntityType.CREEPER) {
            return Items.CREEPER_SPAWN_EGG;
        } else if (entityType == EntityType.ENDERMAN) {
            return Items.ENDERMAN_SPAWN_EGG;
        } else if (entityType == EntityType.SPIDER) {
            return Items.SPIDER_SPAWN_EGG;
        } else if (entityType == EntityType.CAVE_SPIDER) {
            return Items.CAVE_SPIDER_SPAWN_EGG;
        } else if (entityType == EntityType.WITCH) {
            return Items.WITCH_SPAWN_EGG;
        } else if (entityType == EntityType.PILLAGER) {
            return Items.PILLAGER_SPAWN_EGG;
        } else if (entityType == EntityType.VINDICATOR) {
            return Items.VINDICATOR_SPAWN_EGG;
        } else if (entityType == EntityType.EVOKER) {
            return Items.EVOKER_SPAWN_EGG;
        } else if (entityType == EntityType.GHAST) {
            return Items.GHAST_SPAWN_EGG;
        } else if (entityType == EntityType.BLAZE) {
            return Items.BLAZE_SPAWN_EGG;
        } else if (entityType == EntityType.SLIME) {
            return Items.SLIME_SPAWN_EGG;
        } else if (entityType == EntityType.MAGMA_CUBE) {
            return Items.MAGMA_CUBE_SPAWN_EGG;
        } else if (entityType == EntityType.VILLAGER) {
            return Items.VILLAGER_SPAWN_EGG;
        } else if (entityType == EntityType.WANDERING_TRADER) {
            return Items.WANDERING_TRADER_SPAWN_EGG;
        } else if (entityType == EntityType.PIG) {
            return Items.PIG_SPAWN_EGG;
        } else if (entityType == EntityType.COW) {
            return Items.COW_SPAWN_EGG;
        } else if (entityType == EntityType.SHEEP) {
            return Items.SHEEP_SPAWN_EGG;
        } else if (entityType == EntityType.CHICKEN) {
            return Items.CHICKEN_SPAWN_EGG;
        } else if (entityType == EntityType.HORSE) {
            return Items.HORSE_SPAWN_EGG;
        } else if (entityType == EntityType.DONKEY) {
            return Items.DONKEY_SPAWN_EGG;
        } else if (entityType == EntityType.MULE) {
            return Items.MULE_SPAWN_EGG;
        } else if (entityType == EntityType.LLAMA) {
            return Items.LLAMA_SPAWN_EGG;
        } else if (entityType == EntityType.WOLF) {
            return Items.WOLF_SPAWN_EGG;
        } else if (entityType == EntityType.CAT) {
            return Items.CAT_SPAWN_EGG;
        } else if (entityType == EntityType.OCELOT) {
            return Items.OCELOT_SPAWN_EGG;
        } else if (entityType == EntityType.BAT) {
            return Items.BAT_SPAWN_EGG;
        } else if (entityType == EntityType.SQUID) {
            return Items.SQUID_SPAWN_EGG;
        } else if (entityType == EntityType.GLOW_SQUID) {
            return Items.GLOW_SQUID_SPAWN_EGG;
        } else if (entityType == EntityType.DOLPHIN) {
            return Items.DOLPHIN_SPAWN_EGG;
        } else if (entityType == EntityType.TURTLE) {
            return Items.TURTLE_SPAWN_EGG;
        } else if (entityType == EntityType.COD) {
            return Items.COD_SPAWN_EGG;
        } else if (entityType == EntityType.SALMON) {
            return Items.SALMON_SPAWN_EGG;
        } else if (entityType == EntityType.PUFFERFISH) {
            return Items.PUFFERFISH_SPAWN_EGG;
        } else if (entityType == EntityType.TROPICAL_FISH) {
            return Items.TROPICAL_FISH_SPAWN_EGG;
        } else if (entityType == EntityType.HUSK) {
            return Items.HUSK_SPAWN_EGG;
        } else if (entityType == EntityType.DROWNED) {
            return Items.DROWNED_SPAWN_EGG;
        } else {
            return entityType == EntityType.STRAY ? Items.STRAY_SPAWN_EGG : null;
        }
    }

    private String getEntityIcon(EntityType<?> entity) {
        if (entity == EntityType.PLAYER) {
            return "P";
        } else if (entity == EntityType.ZOMBIE || entity == EntityType.HUSK || entity == EntityType.DROWNED) {
            return "Z";
        } else if (entity == EntityType.SKELETON || entity == EntityType.STRAY) {
            return "S";
        } else if (entity == EntityType.CREEPER) {
            return "C";
        } else if (entity == EntityType.ENDERMAN) {
            return "E";
        } else if (entity == EntityType.SPIDER || entity == EntityType.CAVE_SPIDER) {
            return "A";
        } else if (entity == EntityType.WITCH) {
            return "W";
        } else if (entity == EntityType.PILLAGER || entity == EntityType.VINDICATOR || entity == EntityType.EVOKER) {
            return "V";
        } else if (entity == EntityType.ENDER_DRAGON) {
            return "D";
        } else if (entity == EntityType.WITHER) {
            return "B";
        } else if (entity == EntityType.GHAST) {
            return "G";
        } else if (entity == EntityType.BLAZE) {
            return "F";
        } else if (entity == EntityType.SLIME || entity == EntityType.MAGMA_CUBE) {
            return "M";
        } else if (entity == EntityType.IRON_GOLEM || entity == EntityType.SNOW_GOLEM) {
            return "I";
        } else if (entity == EntityType.VILLAGER || entity == EntityType.WANDERING_TRADER) {
            return "N";
        } else if (entity == EntityType.PIG || entity == EntityType.COW || entity == EntityType.SHEEP || entity == EntityType.CHICKEN) {
            return "O";
        } else if (entity == EntityType.HORSE || entity == EntityType.DONKEY || entity == EntityType.MULE || entity == EntityType.LLAMA) {
            return "H";
        } else if (entity == EntityType.WOLF || entity == EntityType.CAT || entity == EntityType.OCELOT) {
            return "L";
        } else if (entity == EntityType.BAT) {
            return "T";
        } else if (entity == EntityType.SQUID || entity == EntityType.GLOW_SQUID || entity == EntityType.DOLPHIN) {
            return "U";
        } else if (entity == EntityType.TURTLE) {
            return "R";
        } else if (entity == EntityType.COD || entity == EntityType.SALMON || entity == EntityType.PUFFERFISH || entity == EntityType.TROPICAL_FISH) {
            return "Q";
        } else if (entity == EntityType.ITEM || entity == EntityType.EXPERIENCE_ORB) {
            return "J";
        } else if (entity == EntityType.ARROW || entity == EntityType.SPECTRAL_ARROW || entity == EntityType.TRIDENT) {
            return "K";
        } else if (entity == EntityType.SNOWBALL || entity == EntityType.EGG || entity == EntityType.ENDER_PEARL) {
            return "Y";
        } else if (entity == EntityType.EXPERIENCE_BOTTLE) {
            return "X";
        } else if (entity == EntityType.FIREBALL || entity == EntityType.SMALL_FIREBALL || entity == EntityType.DRAGON_FIREBALL) {
            return "F";
        } else if (entity == EntityType.WITHER_SKULL) {
            return "B";
        } else if (entity == EntityType.SHULKER_BULLET) {
            return "S";
        } else if (entity == EntityType.LLAMA_SPIT) {
            return "L";
        } else if (entity == EntityType.EVOKER_FANGS) {
            return "V";
        } else if (entity == EntityType.AREA_EFFECT_CLOUD) {
            return "A";
        } else if (entity == EntityType.LIGHTNING_BOLT) {
            return "L";
        } else if (entity == EntityType.MINECART
            || entity == EntityType.CHEST_MINECART
            || entity == EntityType.FURNACE_MINECART
            || entity == EntityType.TNT_MINECART
            || entity == EntityType.HOPPER_MINECART
            || entity == EntityType.SPAWNER_MINECART
            || entity == EntityType.COMMAND_BLOCK_MINECART) {
            return "M";
        } else if (entity == EntityType.ITEM_FRAME || entity == EntityType.GLOW_ITEM_FRAME) {
            return "I";
        } else if (entity == EntityType.PAINTING) {
            return "P";
        } else if (entity == EntityType.ARMOR_STAND) {
            return "A";
        } else {
            return entity == EntityType.LEASH_KNOT ? "L" : "E";
        }
    }

    public static Stream<EntityType<?>> getAllEntities() {
        return Stream.of(
            EntityType.PLAYER,
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.SPIDER,
            EntityType.CAVE_SPIDER,
            EntityType.ENDERMITE,
            EntityType.SILVERFISH,
            EntityType.WITCH,
            EntityType.PILLAGER,
            EntityType.VINDICATOR,
            EntityType.EVOKER,
            EntityType.VEX,
            EntityType.RAVAGER,
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.DROWNED,
            EntityType.PHANTOM,
            EntityType.GHAST,
            EntityType.BLAZE,
            EntityType.MAGMA_CUBE,
            EntityType.SLIME,
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.IRON_GOLEM,
            EntityType.SNOW_GOLEM,
            EntityType.VILLAGER,
            EntityType.WANDERING_TRADER,
            EntityType.VILLAGER,
            EntityType.PIG,
            EntityType.COW,
            EntityType.SHEEP,
            EntityType.CHICKEN,
            EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.LLAMA,
            EntityType.WOLF,
            EntityType.CAT,
            EntityType.OCELOT,
            EntityType.PARROT,
            EntityType.BAT,
            EntityType.SQUID,
            EntityType.GLOW_SQUID,
            EntityType.DOLPHIN,
            EntityType.TURTLE,
            EntityType.COD,
            EntityType.SALMON,
            EntityType.PUFFERFISH,
            EntityType.TROPICAL_FISH,
            EntityType.ITEM,
            EntityType.EXPERIENCE_ORB,
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW,
            EntityType.TRIDENT,
            EntityType.SNOWBALL,
            EntityType.EGG,
            EntityType.ENDER_PEARL,
            EntityType.EXPERIENCE_BOTTLE,
            EntityType.FIREBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.DRAGON_FIREBALL,
            EntityType.WITHER_SKULL,
            EntityType.SHULKER_BULLET,
            EntityType.LLAMA_SPIT,
            EntityType.EVOKER_FANGS,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.LIGHTNING_BOLT,
            EntityType.MINECART,
            EntityType.CHEST_MINECART,
            EntityType.FURNACE_MINECART,
            EntityType.TNT_MINECART,
            EntityType.HOPPER_MINECART,
            EntityType.SPAWNER_MINECART,
            EntityType.COMMAND_BLOCK_MINECART,
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.PAINTING,
            EntityType.ARMOR_STAND,
            EntityType.LEASH_KNOT
        );
    }

        public EntitySelectSetting getSetting() {
        return this.setting;
    }
}
