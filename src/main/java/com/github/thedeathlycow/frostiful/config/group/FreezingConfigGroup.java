package com.github.thedeathlycow.frostiful.config.group;

import com.github.thedeathlycow.frostiful.init.Frostiful;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.math.MathHelper;

@Config(name = Frostiful.MODID + ".freezing")
public class FreezingConfigGroup implements ConfigData {

    boolean doPassiveFreezing = true;
    double biomeTemperatureMultiplier = 4.0;
    double passiveFreezingStartTemp = 0.25;
    double nightTimeTemperatureDecrease = 0.25;
    float passiveFreezingWetnessScaleMultiplier = 2.1f;
    int rainWetnessIncrease = 1;
    int touchingWaterWetnessIncrease = 5;
    int dryRate = 1;
    int onFireDryDate = 50;
    float soakPercentFromWaterPotion = 0.5f;
    int cannotFreezeThawRate = 100;
    int onFireThawRate = 50;
    int conduitPowerWarmthPerTick = 12;
    int warmthPerLightLevel = 4;
    int minLightForWarmth = 5;
    int freezeDamageRate = 20;
    int freezeDamageAmount = 2;
    int freezeDamageExtraAmount = 5;
    int powderSnowFreezeRate = 30;
    int sunLichenHeatPerLevel = 100;
    double campfireWarmthSearchRadius = 10;
    int campfireWarmthTime = 1200;

    int ultrawarmThawRate = 15;

    public boolean doPassiveFreezing() {
        return doPassiveFreezing;
    }

    public double getBiomeTemperatureMultiplier() {
        return biomeTemperatureMultiplier;
    }

    public double getPassiveFreezingStartTemp() {
        return passiveFreezingStartTemp;
    }

    public double getNightTimeTemperatureDecrease() {
        return nightTimeTemperatureDecrease;
    }

    public float getPassiveFreezingWetnessScaleMultiplier() {
        return passiveFreezingWetnessScaleMultiplier;
    }

    public int getRainWetnessIncrease() {
        return rainWetnessIncrease;
    }

    public int getTouchingWaterWetnessIncrease() {
        return touchingWaterWetnessIncrease;
    }

    public int getDryRate() {
        return dryRate;
    }

    public int getOnFireDryRate() {
        return onFireDryDate;
    }

    public float getSoakPercentFromWaterPotion() {
        return MathHelper.clamp(soakPercentFromWaterPotion, 0.0f, 1.0f);
    }

    public int getCannotFreezeThawRate() {
        return cannotFreezeThawRate;
    }

    public int getOnFireThawRate() {
        return onFireThawRate;
    }

    public int getConduitPowerWarmthPerTick() {
        return conduitPowerWarmthPerTick;
    }

    public int getWarmthPerLightLevel() {
        return warmthPerLightLevel;
    }

    public int getMinLightForWarmth() {
        return minLightForWarmth;
    }

    public int getFreezeDamageRate() {
        return freezeDamageRate;
    }

    public int getFreezeDamageAmount() {
        return freezeDamageAmount;
    }

    public int getFreezeDamageExtraAmount() {
        return freezeDamageExtraAmount;
    }

    public int getPowderSnowFreezeRate() {
        return powderSnowFreezeRate;
    }

    public int getSunLichenHeatPerLevel() {
        return sunLichenHeatPerLevel;
    }

    public double getCampfireWarmthSearchRadius() {
        return campfireWarmthSearchRadius;
    }

    public int getCampfireWarmthTime() {
        return campfireWarmthTime;
    }

    public int getUltrawarmThawRate() {
        return ultrawarmThawRate;
    }
}
