/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class AFKConfig {

    @Setting(value = "afktime", comment = "loc:config.afk.time")
    private long afkTime = 300;

    @Setting(value = "afktimetokick", comment = "loc:config.afk.timetokick")
    private long afkTimeToKick = 0;

    @Setting(value = "afk-when-vanished", comment = "loc:config.afk.whenvanished")
    private boolean afkOnVanish = false;

    public long getAfkTime() {
        return afkTime;
    }

    public long getAfkTimeToKick() {
        return afkTimeToKick;
    }

    public boolean isAfkOnVanish() {
        return afkOnVanish;
    }
}
