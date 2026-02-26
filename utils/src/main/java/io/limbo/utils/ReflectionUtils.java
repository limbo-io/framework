/*
 * Copyright 2025-2030 Fluxion Team (https://github.com/Fluxion-io).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.limbo.utils;

import org.reflections.Reflections;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Devil
 */
public class ReflectionUtils {

    private static Reflections REFLECTIONS = new Reflections();
    private static final Object LOCK = new Object();

    /**
     * T<R> cache T -> R
     */
    private static final Map<Class<?>, Class<?>> TYPE_CACHE = new ConcurrentHashMap<>();

    static {
        // Try to load configuration via SPI
        try {
            ServiceLoader<Configuration> loader = ServiceLoader.load(Configuration.class);
            for (Configuration config : loader) {
                String[] packages = config.getScanPackages();
                if (packages != null && packages.length > 0) {
                    configure(packages);
                    break;
                }
            }
        } catch (Exception e) {
            // Ignore SPI loading errors, use default configuration
        }
    }

    /**
     * 配置 Reflections 扫描的包路径
     *
     * @param packages 要扫描的包路径数组
     */
    public static void configure(String... packages) {
        if (packages == null || packages.length == 0) {
            return;
        }
        synchronized (LOCK) {
            REFLECTIONS = new Reflections(packages);
        }
    }

    /**
     * 配置自定义的 Reflections 实例
     *
     * @param reflections 自定义 Reflections 实例
     */
    public static void configure(Reflections reflections) {
        if (reflections == null) {
            return;
        }
        synchronized (LOCK) {
            REFLECTIONS = reflections;
        }
    }

    /**
     * 获取某个类的子类型
     */
    public static <T> Set<Class<? extends T>> subTypesOf(Class<T> type) {
        return REFLECTIONS.getSubTypesOf(type);
    }

    /**
     * 获取某个类的泛型类型
     */
    public static <R, Q> Class<R> refType(Q obj) {
        return (Class<R>) TYPE_CACHE.computeIfAbsent(
            obj.getClass(),
            clazz -> (Class<R>) ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0]
        );
    }

    /**
     * ReflectionUtils 配置接口
     * 用户可以通过实现此接口并使用 SPI 机制来自定义扫描包路径
     *
     * @author Devil
     */
    public interface Configuration {

        /**
         * 获取要扫描的包路径数组
         *
         * @return 包路径数组，如 {"io.fluxon", "com.example"}
         */
        String[] getScanPackages();
    }


}
