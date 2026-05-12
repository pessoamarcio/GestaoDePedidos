package sistemapedidos.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class StreamUtils {

	private StreamUtils() {
	}

	public static <K> Set<K> unionKeys(Map<K, ?> left, Map<K, ?> right) {
		Set<K> result = new HashSet<>(left.keySet());
		result.addAll(right.keySet());
		return result;
	}
}

