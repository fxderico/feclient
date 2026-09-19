package dev.fede.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

/**
 * /viewcrds <player> — quick action-bar readout (bottom-center, same spot
 * as vanilla's own action-bar messages) of a player's coordinates. Only
 * works for players your client is actually currently tracking (i.e. ones
 * the server has sent position data for) — there's no coordinate to show
 * for someone your client has never received a packet about, "always
 * load" or not; see the View module's docs for why.
 */
public final class ViewCoordsCommand {
   private ViewCoordsCommand() {
   }

   public static void register() {
      ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
         dispatcher.register(
            ClientCommandManager.literal("viewcrds")
               .then(
                  ClientCommandManager.argument("player", StringArgumentType.word())
                     .executes(ViewCoordsCommand::run)
               )
         )
      );
   }

   private static int run(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
      FabricClientCommandSource source = ctx.getSource();
      String name = StringArgumentType.getString(ctx, "player");
      ClientWorld world = source.getWorld();

      AbstractClientPlayerEntity target = null;
      for (AbstractClientPlayerEntity player : world.getPlayers()) {
         if (player.getGameProfile().name().equalsIgnoreCase(name)) {
            target = player;
            break;
         }
      }

      if (target == null) {
         source.sendError(Text.literal("'" + name + "' isn't a visible player right now."));
         return 0;
      }

      String coords = String.format(
         "§e%s §7— §fX: §e%.1f §7Y: §e%.1f §7Z: §e%.1f",
         target.getGameProfile().name(), target.getX(), target.getY(), target.getZ()
      );
      source.getPlayer().sendMessage(Text.literal(coords), true);
      return 1;
   }
}
