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
import java.lang.reflect.Type;
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
     * <p>
     * 遍历类的所有泛型接口，找到第一个 ParameterizedType 并返回其第一个类型参数。
     * 如果类继承泛型父类，也会尝试从父类获取。
     *
     * @param obj 实现了泛型接口的对象
     * @return 泛型类型参数对应的 Class
     * @throws IllegalArgumentException 如果无法解析泛型类型
     */
    @SuppressWarnings("unchecked")
    public static <R, Q> Class<R> refType(Q obj) {
        return (Class<R>) TYPE_CACHE.computeIfAbsent(obj.getClass(), ReflectionUtils::resolveGenericType);
    }

    /**
     * 解析类的泛型类型参数
     */
    private static <R> Class<R> resolveGenericType(Class<?> clazz) {
        // 1. 先从泛型接口中查找（包括接口继承链）
        Class<R> typeArg = resolveFromInterfaces(clazz);
        if (typeArg != null) {
            return typeArg;
        }

        // 2. 从泛型父类中查找
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass != null && genericSuperclass != Object.class) {
            // 2.1 如果父类是 ParameterizedType，直接提取类型参数
            typeArg = extractTypeArgument(genericSuperclass);
            if (typeArg != null) {
                return typeArg;
            }
            // 2.2 如果父类是普通 Class，递归解析其父类的泛型信息
            if (genericSuperclass instanceof Class) {
                return resolveGenericType((Class<?>) genericSuperclass);
            }
        }

        throw new IllegalArgumentException(
            "Cannot resolve generic type for class: " + clazz.getName() +
            ". Ensure it implements a parameterized interface or extends a parameterized class."
        );
    }

    /**
     * 从类的所有接口（包括继承的接口）中解析泛型类型
     */
    private static <R> Class<R> resolveFromInterfaces(Class<?> clazz) {
        // 使用广度优先搜索遍历所有接口
        java.util.Queue<Type> queue = new java.util.LinkedList<>();
        java.util.Set<Class<?>> visited = new java.util.HashSet<>();

        // 初始化队列，加入直接实现的接口
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            Class<R> typeArg = extractTypeArgument(genericInterface);
            if (typeArg != null) {
                return typeArg;
            }
            // 无论是否是 ParameterizedType，都加入队列继续查找
            queue.offer(genericInterface);
        }

        // BFS 遍历所有父接口
        while (!queue.isEmpty()) {
            Type currentType = queue.poll();

            // 获取当前类型的 Class（用于 getGenericInterfaces）
            Class<?> currentClass;
            if (currentType instanceof Class) {
                currentClass = (Class<?>) currentType;
            } else if (currentType instanceof ParameterizedType) {
                currentClass = (Class<?>) ((ParameterizedType) currentType).getRawType();
            } else {
                continue; // 跳过其他类型（如 WildcardType, TypeVariable 等）
            }

            // 如果已经访问过这个 Class，跳过
            if (!visited.add(currentClass)) {
                continue;
            }

            for (Type parentInterface : currentClass.getGenericInterfaces()) {
                Class<R> typeArg = extractTypeArgument(parentInterface);
                if (typeArg != null) {
                    return typeArg;
                }
                queue.offer(parentInterface);
            }
        }

        return null;
    }

    /**
     * 从 Type 中提取第一个类型参数
     */
    @SuppressWarnings("unchecked")
    private static <R> Class<R> extractTypeArgument(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] actualArgs = ((ParameterizedType) type).getActualTypeArguments();
            if (actualArgs.length > 0 && actualArgs[0] instanceof Class) {
                return (Class<R>) actualArgs[0];
            }
        }
        return null;
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
