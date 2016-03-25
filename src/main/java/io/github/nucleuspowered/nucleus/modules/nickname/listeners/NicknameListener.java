/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.config.loaders.UserConfigLoader;
import io.github.nucleuspowered.nucleus.internal.ListenerBase;
import io.github.nucleuspowered.nucleus.internal.interfaces.InternalNucleusUser;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class NicknameListener extends ListenerBase {

    @Inject private UserConfigLoader ucl;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        InternalNucleusUser iqsu;
        try {
            iqsu = ucl.getUser(player);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Optional<Text> d = iqsu.getNicknameAsText();
        if (d.isPresent()) {
            player.offer(Keys.DISPLAY_NAME, d.get());
        } else {
            player.remove(Keys.DISPLAY_NAME);
        }
    }
}