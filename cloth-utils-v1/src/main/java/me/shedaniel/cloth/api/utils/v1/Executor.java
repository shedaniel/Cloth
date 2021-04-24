/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org>
 */

package me.shedaniel.cloth.api.utils.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public final class Executor {
    private Executor() {}
    
    public static void run(Supplier<Runnable> runnableSupplier) {
        runnableSupplier.get().run();
    }
    
    public static void runIf(Supplier<Boolean> predicate, Supplier<Runnable> runnableSupplier) {
        if (predicate.get())
            runnableSupplier.get().run();
    }
    
    public static void runIfEnv(EnvType env, Supplier<Runnable> runnableSupplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == env)
            runnableSupplier.get().run();
    }
    
    public static <T> T call(Supplier<Callable<T>> runnableSupplier) {
        try {
            return runnableSupplier.get().call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T callForEnv(Supplier<Callable<T>> client, Supplier<Callable<T>> common) {
        return call(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? client : common);
    }
    
    public static <T> Optional<T> callIf(Supplier<Boolean> predicate, Supplier<Callable<T>> runnableSupplier) {
        if (predicate.get())
            try {
                return Optional.of(runnableSupplier.get().call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return Optional.empty();
    }
    
    public static <T> Optional<T> callIfEnv(EnvType env, Supplier<Callable<T>> runnableSupplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == env)
            try {
                return Optional.of(runnableSupplier.get().call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return Optional.empty();
    }

    public static ModExecutor getModExecutor(String modid) {
        return ModExecutor.EXECUTOR_MAP.getOrDefault(modid, new ModExecutor(modid));
    }
}
