package net.wurstclient.forge.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.other.customs.ClickGUIButtonMainMenu;
import net.wurstclient.forge.other.customs.UnlimitedTextField;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.List;

public class GUITweaks {
    @SubscribeEvent
    public void onGUI(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu) {
            Minecraft mc = Minecraft.getMinecraft();

            // Draw Wurst logo at top left
            ResourceLocation resourceLocation1 = new ResourceLocation(ForgeWurst.MODID, "wurst-logo.png");
            mc.getTextureManager().bindTexture(resourceLocation1);

            ScaledResolution sr = new ScaledResolution(mc);
            int logoWidth1 = 64 * sr.getScaleFactor();
            int logoHeight1 = 32 * sr.getScaleFactor();

            int logoX = 10;
            int logoY = 10;

            Gui.drawModalRectWithCustomSizedTexture(logoX, logoY, 0, 0, logoWidth1, logoHeight1, logoWidth1, logoHeight1);

            // Draw "FORGEWURST" text under Wurst logo
            String text = "FORGEWURST";
            int textWidth = mc.fontRenderer.getStringWidth(text);
            int textX = logoX + (logoWidth1 - textWidth) / 2;
            int textY = logoY + logoHeight1 + 5;

            mc.fontRenderer.drawStringWithShadow(text, textX, textY, 0xFFFFFF);

            // Draw Fallen logo at top right
            ResourceLocation resourceLocation2 = new ResourceLocation(ForgeWurst.MODID, "fallen-logo.png");
            mc.getTextureManager().bindTexture(resourceLocation2);

            int logoWidth2 = 64 * sr.getScaleFactor();
            int logoHeight2 = 32 * sr.getScaleFactor();

            int logo2X = sr.getScaledWidth() - logoWidth2 - 10;
            int logo2Y = 10;

            Gui.drawModalRectWithCustomSizedTexture(logo2X, logo2Y, 0, 0, logoWidth2, logoHeight2, logoWidth2, logoHeight2);

            // Draw "Australian Made" text under Fallen logo
            String fText = "Australian Made";
            int fTextWidth = mc.fontRenderer.getStringWidth(text);
            int fTextX = logo2X + (logoWidth2 - fTextWidth) / 2;
            int fTextY = logo2Y + logoHeight2 + 5;

            mc.fontRenderer.drawStringWithShadow(fText, fTextX + 5, fTextY - 15, 0xFFFFFF);

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            int bottomHalfY = screenHeight / 2 + screenHeight / 4;

            int x = screenWidth / 2 - (500 / 2); // Calculate the x-coordinate for the button
            int y = bottomHalfY - (25 / 2); // Calculate the y-coordinate for the button at the bottom center of the screen

            UnlimitedTextField unlimitedTextField = new UnlimitedTextField(44, Minecraft.getMinecraft().fontRenderer, x, y + 110, 500, 25);
            unlimitedTextField.setText(whatsTheBuzzTellMeWhatsAHappening());
            unlimitedTextField.setVisible(true);
            unlimitedTextField.setEnabled(false);
            unlimitedTextField.setEnableBackgroundDrawing(true);
            unlimitedTextField.drawTextBox();
        }
    }

    @SubscribeEvent
    public void onGUI(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            int buttonWidth = 100;
            int buttonHeight = 20;
            int buttonMargin = 5; // Margin between buttons

            List<GuiButton> buttonList = event.getButtonList();
            int numButtons = buttonList.size();

            int buttonX = event.getGui().width / 2 - (buttonWidth / 2); // Center the button horizontally
            int buttonY = event.getGui().height / 2 + (numButtons * (buttonHeight + buttonMargin)) - 140; // Position the button below the existing buttons with a margin

            GuiButton guiButton = new ClickGUIButtonMainMenu(numButtons + 1, buttonX, buttonY, buttonWidth, buttonHeight, "ClickGUI");

            guiButton.packedFGColour = 0xadd8e6;

            event.getButtonList().add(guiButton);
        }
    }

    private String whatsTheBuzzTellMeWhatsAHappening() {
        // Max: 80 chars
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so adding 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int bottomHalfY = screenHeight / 2 + screenHeight / 4;

        int x = screenWidth / 2 - (500 / 2);
        int y = bottomHalfY - (25 / 2);

        if (month == 12 && day == 15) {
            return "Today, we celebrate Terry Davis's birth!.";
        } else if (month == 8 && day == 11) {
            return "Today, we honor Terry Davis. May his soul rest in peace.";
        } else if (month == 10 && day == 8) {
            return "On this day in 2021, Fallen was established!.";
        } else if (month == 12 && day == 5) {
            return "[Paul - Founder] I was born today!";
        } else if (month == 10 && day == 12) {
            return "[RickMondy - Trusted Confidant] I was born today!";
        } else if (month == 5 && day == 16) {
            return "[Funny - Resilient Patron] I was born today!";
        } else if (month == 7 && day == 11) {
            return "[Zamplex - Early Adopter] I was born today!";
        } else if (month == 1 && day == 1) {
            return "Happy new years!";
        } else if (month == 12 && day == 25) {
            return "Merry Christmas!";
        } else if (month == 11 && day == 19) {
            return "Its International Men's Day!";
        } else if (month == 1 && day == 26) {
            return "Happy Australia Day! Fallen is Australian Made.";
        } else if (month == 2 && day == 9) {
            return "Happy Birthday Alexander! Thank you for wurst.";
        } else {
            return "Upcoming events: " + getNearestEvent(month, day);
        }
    }

    public static String getNearestEvent(int currentMonth, int currentDay) {
        LocalDate currentDate = LocalDate.of(LocalDate.now().getYear(), currentMonth, currentDay);

        LocalDate terryDavisBirth = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 15);
        LocalDate terryDavisDeath = LocalDate.of(LocalDate.now().getYear(), Month.AUGUST, 11);
        LocalDate fallenEstablished = LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 8);
        LocalDate paulBirthday = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 5);
        LocalDate rickMondyBirthday = LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 12);
        LocalDate funnyBirthday = LocalDate.of(LocalDate.now().getYear(), Month.MAY, 16);
        LocalDate zamplexBirthday = LocalDate.of(LocalDate.now().getYear(), Month.JULY, 11);
        LocalDate newYearsDay = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);
        LocalDate christmasDay = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 25);
        LocalDate internationalMensDay = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 19);
        LocalDate australiaDay = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 26);
        LocalDate alexandersBirthday = LocalDate.of(LocalDate.now().getYear(), Month.FEBRUARY, 9);

        LocalDate[] eventDates = {terryDavisBirth, terryDavisDeath, fallenEstablished, paulBirthday, rickMondyBirthday,
                funnyBirthday, zamplexBirthday, newYearsDay, christmasDay, internationalMensDay, australiaDay, alexandersBirthday};

        LocalDate nearestEventDate = null;
        long nearestEventDays = Long.MAX_VALUE;

        for (LocalDate eventDate : eventDates) {
            if (eventDate.isAfter(currentDate) && eventDate.toEpochDay() - currentDate.toEpochDay() < nearestEventDays) {
                nearestEventDate = eventDate;
                nearestEventDays = eventDate.toEpochDay() - currentDate.toEpochDay();
            }
        }

        if (nearestEventDate != null) {
            if (nearestEventDate.equals(terryDavisBirth)) {
                return "Terry Davis's birth" + " | " + terryDavisBirth;
            } else if (nearestEventDate.equals(terryDavisDeath)) {
                return "Terry Davis's death." + " | " + terryDavisDeath;
            } else if (nearestEventDate.equals(fallenEstablished)) {
                return "Fallen established" + " | " + fallenEstablished;
            } else if (nearestEventDate.equals(paulBirthday)) {
                return "Paul's birth." + " | " + paulBirthday;
            } else if (nearestEventDate.equals(rickMondyBirthday)) {
                return "RickMondy's birth." + " | " + rickMondyBirthday;
            } else if (nearestEventDate.equals(funnyBirthday)) {
                return "Funny's birth." + " | " + funnyBirthday;
            } else if (nearestEventDate.equals(zamplexBirthday)) {
                return "Zamplex's birth." + " | " + zamplexBirthday;
            } else if (nearestEventDate.equals(newYearsDay)) {
                return "New years" + " | " + newYearsDay;
            } else if (nearestEventDate.equals(christmasDay)) {
                return "Christmas" + " | " + christmasDay;
            } else if (nearestEventDate.equals(internationalMensDay)) {
                return "International Men's Day" + " | " + internationalMensDay;
            } else if (nearestEventDate.equals(australiaDay)) {
                return "Australia Day" + " | " + australiaDay;
            } else if (nearestEventDate.equals(alexandersBirthday)) {
                return "Alexanders Birthday" + " | " + alexandersBirthday;
            }
        }

        return "Upcoming events: ";
    }
}