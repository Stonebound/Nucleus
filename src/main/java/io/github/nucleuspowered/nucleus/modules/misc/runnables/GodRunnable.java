/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.runnables;

import io.github.nucleuspowered.nucleus.Nucleus;
import io.github.nucleuspowered.nucleus.internal.TaskBase;
import io.github.nucleuspowered.nucleus.modules.misc.datamodules.InvulnerabilityUserDataModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Temporary solution until we can check for feeding levels to drop.
 */
@NonnullByDefault
public class GodRunnable extends TaskBase {

    private int defaultFoodLevel = 0;
    private double defaultSaturationLevel = 0;
    private double defaultExhaustionLevel = 0;

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public Duration interval() {
        return Duration.of(5, ChronoUnit.SECONDS);
    }

    @Override
    public void accept(Task task) {
        Collection<Player> cp = Sponge.getServer().getOnlinePlayers();
        if (cp.isEmpty()) {
            return;
        }

        if (this.defaultFoodLevel == 0) {
            FoodData def = cp.iterator().next().getFoodData();
            this.defaultFoodLevel = def.foodLevel().getDefault();
            this.defaultSaturationLevel = def.saturation().getDefault();
            this.defaultExhaustionLevel = def.exhaustion().getDefault();
        }

        List<Player> toFeed = cp.stream().filter(x -> Nucleus.getNucleus().getUserDataManager().getUser(x)
                .map(y -> y.get(InvulnerabilityUserDataModule.class).isInvulnerable()).orElse(false))
                .collect(Collectors.toList());
        if (!toFeed.isEmpty()) {
            Sponge.getScheduler().createSyncExecutor(Nucleus.getNucleus()).execute(() -> toFeed.forEach(p -> {
                p.offer(Keys.FOOD_LEVEL, this.defaultFoodLevel);
                p.offer(Keys.EXHAUSTION, this.defaultExhaustionLevel);
                p.offer(Keys.SATURATION, this.defaultSaturationLevel);
            }));
        }
    }
}
