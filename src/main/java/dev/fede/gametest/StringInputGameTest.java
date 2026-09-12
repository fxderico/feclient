package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.gui.ClickGuiScreen;
import dev.fede.gui.widget.StringWidget;
import dev.fede.module.impl.FakePayModule;
import dev.fede.settings.StringSetting;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class StringInputGameTest implements FabricClientGameTest {
   private static final String EURO = codePoint(8364);
   private static final String POUND = codePoint(163);
   private static final String YEN = codePoint(165);

   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1600, 900);
         context.waitTicks(2);
         context.runOnClient(mc -> {
            FakePayModule fp = FeClient.modules().fakePay;
            require(fp != null, "FakePay registered");
            StringSetting currency = fp.currency;
            StringWidget widget = new StringWidget(FeClient.themes(), currency);
            widget.setBounds(0.0F, 0.0F, 200.0F);
            require(widget.mouseClicked(5.0F, 5.0F, 0), "left click focuses the field");
            require(widget.isListening(), "focused field listens for typing");
            currency.set("");
            type(widget, EURO);
            require(currency.get().equals(EURO), "euro sign typed, got '" + currency.get() + "'");
            currency.set("");
            type(widget, "nullnull");
            require(currency.get().equals("nullnull"), "pound+yen typed, got '" + currency.get() + "'");
            currency.set("$");
            widget.keyPressed(259);
            require(currency.get().isEmpty(), "backspace cleared the '$'");
            type(widget, "$");
            require(currency.get().equals("$"), "'$' retyped after clearing, got '" + currency.get() + "'");
            currency.set("");
            type(widget, "USD");
            require(currency.get().equals("USD"), "letters still accepted, got '" + currency.get() + "'");
            currency.set("");
            type(widget, "$$$$$$");
            require(currency.get().length() == 4, "maxLength caps at 4, got " + currency.get().length());
            currency.set(EURO);
         });
         context.runOnClient(mc -> mc.setScreen(new ClickGuiScreen()));
         context.waitTicks(3);
         context.takeScreenshot("stringinput-clickgui-open");
         context.runOnClient(mc -> mc.setScreen(null));
         context.waitTicks(2);
         context.runOnClient(mc -> {
            FakePayModule fp = FeClient.modules().fakePay;
            fp.feedback.set("Both");
            fp.setEnabled(true);
            require(fp.currency.get().equals(EURO), "currency persisted as euro");
            mc.player.networkHandler.sendChatCommand("pay Notch 250k");
         });
         context.waitTicks(2);
         context.takeScreenshot("stringinput-fakepay-euro-receipt");
      } catch (Throwable var6) {
         if (world != null) {
            try {
               world.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (world != null) {
         world.close();
      }
   }

   private static String codePoint(int cp) {
      return new String(Character.toChars(cp));
   }

   private static void type(StringWidget widget, String text) {
      text.codePoints().forEach(widget::charTyped);
   }

   private static void require(boolean condition, String what) {
      if (!condition) {
         throw new AssertionError("FAILED: null");
      }
   }
}



