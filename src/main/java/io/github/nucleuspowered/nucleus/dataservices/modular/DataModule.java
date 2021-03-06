/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.dataservices.modular;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.Nucleus;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * THIS MUST HAVE A NO-ARGS CONSTRUCTOR.
 *
 * @param <S> The {@link ModularDataService} that this represents.
 */
public abstract class DataModule<S extends ModularDataService<S>> {

    private static final Map<Class<? extends DataModule<?>>, List<FieldData>> fieldData = Maps.newHashMap();

    private final List<FieldData> data;

    @SuppressWarnings("unchecked") protected DataModule() {
        data = fieldData.computeIfAbsent((Class<? extends DataModule<?>>) this.getClass(), this::init);
    }

    void loadFrom(ConfigurationNode node) {
        for (FieldData d : data) {
            try {
                Optional<?> value = getValue(d.clazz, d.path, node);
                if (value.isPresent()) {
                    d.field.set(this, value.get());
                }
            } catch (IllegalArgumentException e) {
                // ignored, we'll stick with the default.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // We loaded, migrate anything that needs to be migrated.
        migrate();
    }

    /**
     * Migrate data
     */
    protected void migrate() {
        // noop
    }

    private <T> Optional<T> getValue(TypeToken<T> token, String[] path, ConfigurationNode node) {
        try {
            return Optional.ofNullable(node.getNode((Object[]) path).getValue(token));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    void saveTo(ConfigurationNode node) {
        for (FieldData d : data) {
            try {
                getObj(d.clazz, d.field, d.path, node);
            } catch (Exception e) {
                Nucleus.getNucleus().getLogger().error("Could not save module " + d.clazz.getType().getTypeName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void getObj(TypeToken<T> typeToken, Field field, String[] path, ConfigurationNode node) throws ObjectMappingException {
        T t;
        try {
            t = (T)field.get(this);
        } catch (IllegalAccessException e) {
            Nucleus.getNucleus().getLogger().error("Could not get data from " + getClass().getSimpleName() + ": " + field.getName(), e);
            t = null;
        }

        node.getNode((Object[])path).setValue(typeToken, t);
    }

    private List<FieldData> init(Class<? extends DataModule<?>> clazz) {
        // Get the fields.
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
            .filter(x -> x.isAnnotationPresent(DataKey.class))
            .collect(Collectors.toList());

        fields.forEach(x -> x.setAccessible(true));
        return fields.stream().map(x -> new FieldData(x.getAnnotation(DataKey.class).value(), TypeToken.of(x.getGenericType()), x)).collect(Collectors.toList());
    }

    /**
     * THE CONSTRUCTOR OF THE SUBCLASS MUST HAVE THE SAME FORM AS THIS!
     *
     * @param <T> The {@link ModularDataService} class that this will reference.
     */
    public static abstract class ReferenceService<T extends ModularDataService<T>> extends DataModule<T> {

        private final WeakReference<T> modularDataService;

        @Nonnull
        protected T getService() {
            return Preconditions.checkNotNull(modularDataService.get());
        }

        public ReferenceService(T modularDataService) {
            this.modularDataService = new WeakReference<>(modularDataService);
        }
    }

    private static class FieldData {

        private final String[] path;
        private final TypeToken<?> clazz;
        private final Field field;

        private FieldData(String[] path, TypeToken<?> clazz, Field field) {
            this.path = path;
            this.clazz = clazz;
            this.field = field;
        }
    }
}
