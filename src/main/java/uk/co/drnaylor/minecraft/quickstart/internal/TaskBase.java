/*
 * This file is part of QuickStart, licensed under the MIT License (MIT). See the LICENCE.txt file
 * at the root of this project for more details.
 */
package uk.co.drnaylor.minecraft.quickstart.internal;

import org.spongepowered.api.scheduler.Task;
import uk.co.drnaylor.minecraft.quickstart.internal.permissions.PermissionInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class TaskBase implements Consumer<Task> {

    public abstract boolean isAsync();

    public abstract int secondsPerRun();

    protected Map<String, PermissionInformation> getPermissions() {
        return new HashMap<>();
    }
}
