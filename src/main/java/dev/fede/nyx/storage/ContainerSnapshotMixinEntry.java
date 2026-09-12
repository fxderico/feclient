package dev.fede.nyx.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Base64;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public final class ContainerSnapshotMixinEntry {
   public String itemId;
   public int count;
   public String nbt;

   public ContainerSnapshotMixinEntry() {
   }

   public ContainerSnapshotMixinEntry(String var1, int var2, String var3) {
      this.itemId = var1;
      this.count = var2;
      this.nbt = var3 == null ? "" : var3;
   }

   public static ContainerSnapshotMixinEntry from(ItemStack var0, DynamicRegistryManager var1) {
      if (var0 != null && !var0.isEmpty()) {
         Identifier var2 = Registries.ITEM.getId(var0.getItem());
         String var3 = var2 == null ? "minecraft:air" : var2.toString();
         int var4 = var0.getCount();
         String var5 = encodeNbt(var0, var1);
         return new ContainerSnapshotMixinEntry(var3, var4, var5);
      } else {
         return null;
      }
   }

   public ItemStack toStack() {
      if (this.itemId != null && !this.itemId.isEmpty()) {
         Identifier var1 = Identifier.tryParse(this.itemId);
         if (var1 == null) {
            return ItemStack.EMPTY;
         } else {
            Item var2 = (Item)Registries.ITEM.get(var1);
            if (var2 == null) {
               return ItemStack.EMPTY;
            } else {
               int var3 = Math.max(1, this.count);
               return new ItemStack(var2, var3);
            }
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private static String encodeNbt(ItemStack var0, DynamicRegistryManager var1) {
      if (var1 == null) {
         return "";
      } else {
         try {
            RegistryOps var2 = var1.getOps(NbtOps.INSTANCE);
            NbtElement var3 = (NbtElement)ItemStack.CODEC.encodeStart(var2, var0).result().orElse(null);
            if (var3 == null) {
               return "";
            } else {
               NbtCompound var4;
               if (var3 instanceof NbtCompound var5) {
                  var4 = var5;
               } else {
                  var4 = new NbtCompound();
                  var4.put("v", var3);
               }

               String var7;
               try (
                  ByteArrayOutputStream var14 = new ByteArrayOutputStream();
                  DataOutputStream var6 = new DataOutputStream(var14);
               ) {
                  NbtIo.writeCompressed(var4, var6);
                  var7 = Base64.getEncoder().encodeToString(var14.toByteArray());
               }

               return var7;
            }
         } catch (Throwable var13) {
            return "";
         }
      }
   }

   public NbtCompound decodeNbt() {
      if (this.nbt != null && !this.nbt.isEmpty()) {
         try {
            byte[] var1 = Base64.getDecoder().decode(this.nbt);

            NbtCompound var4;
            try (
               ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
               DataInputStream var3 = new DataInputStream(var2);
            ) {
               var4 = NbtIo.readCompressed(var3, NbtSizeTracker.ofUnlimitedBytes());
            }

            return var4;
         } catch (Throwable var10) {
            return null;
         }
      } else {
         return null;
      }
   }
}

