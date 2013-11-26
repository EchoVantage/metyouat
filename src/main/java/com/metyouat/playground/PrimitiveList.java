package com.metyouat.playground;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.List;

public class PrimitiveList<T> extends AbstractList<T> {
	public static List<Boolean> asList(final boolean[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Byte> asList(final byte[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Character> asList(final char[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Double> asList(final double[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Float> asList(final float[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Integer> asList(final int[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Long> asList(final long[] array) {
		return new PrimitiveList<>(array);
	}

	public static List<Short> asList(final short[] array) {
		return new PrimitiveList<>(array);
	}

	private final Object array;

	private PrimitiveList(final Object array) {
		this.array = array;
	}

	@Override
	public T get(final int index) {
		return (T) Array.get(array, index);
	}

	@Override
	public int size() {
		return Array.getLength(array);
	}
}