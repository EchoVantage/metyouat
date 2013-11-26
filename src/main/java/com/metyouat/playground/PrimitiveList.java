package com.metyouat.playground;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.List;

public class PrimitiveList<T> extends AbstractList<T> {
	public static List<Long> asList(long[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Boolean> asList(boolean[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Integer> asList(int[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Byte> asList(byte[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Short> asList(short[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Character> asList(char[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Float> asList(float[] array){
		return new PrimitiveList<>(array);
	}
	
	public static List<Double> asList(double[] array){
		return new PrimitiveList<>(array);
	}
	
   private final Object array;

   private PrimitiveList(Object array) {
	   this.array = array;
   }

   @Override
   public T get(int index) {
      return (T)Array.get(array, index);
   }

   @Override
   public int size() {
      return Array.getLength(array);
   }
}