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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Represents an executor that only runs tasks if a certain mod
 * is present.
 *
 * <p><b>All</b> tasks run on the same thread of the caller</p>
 */
public class ModExecutor implements Executor {
	static final Map<String, ModExecutor> EXECUTOR_MAP = new HashMap<>();
	private final String modid;

	ModExecutor(String modid) {
		this.modid = modid;
		EXECUTOR_MAP.put(modid, this);
	}

	@Override
	public void execute(Runnable command) {
		if (FabricLoader.getInstance().isModLoaded(this.modid)) {
			command.run();
		}
	}

	public <T> Optional<T> submit(Callable<T> task) throws Exception {
		if (FabricLoader.getInstance().isModLoaded(this.modid)) {
			return Optional.ofNullable(task.call());
		}

		return Optional.empty();
	}

	public <T> Optional<T> submitQuietly(Callable<T> task) {
		try {
			return this.submit(task);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception caught in " + this.toString() , e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ModExecutor)) return false;
		ModExecutor that = (ModExecutor) o;
		return Objects.equals(this.modid, that.modid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.modid);
	}

	@Override
	public String toString() {
		return "ModExecutor{" + "modid='" + this.modid + '\'' + '}';
	}
}
